package com.simibubi.create.content.redstone.displayLink;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.utility.CreateLang;

import io.netty.buffer.ByteBuf;
import net.createmod.catnip.nbt.NBTHelper;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class ClickToLinkBlockItem extends BlockItem {
	public ClickToLinkBlockItem(Block pBlock, Properties pProperties) {
		super(pBlock, pProperties);
	}

	public static InteractionResult linkableItemAlwaysPlacesWhenUsed(Player player, Level level, InteractionHand hand, BlockHitResult hit) {
		ItemStack usedItem = player.getItemInHand(hand);
		if (!(usedItem.getItem() instanceof ClickToLinkBlockItem blockItem))
			return InteractionResult.PASS;
		if (level.getBlockState(hit.getBlockPos()).is(blockItem.getBlock()))
			return InteractionResult.PASS;

		return InteractionResult.FAIL;
	}

	@Override
	public InteractionResult useOn(UseOnContext pContext) {
		ItemStack stack = pContext.getItemInHand();
		BlockPos pos = pContext.getClickedPos();
		Level level = pContext.getLevel();
		BlockState state = level.getBlockState(pos);
		Player player = pContext.getPlayer();
		String msgKey = getMessageTranslationKey();
		int maxDistance = getMaxDistanceFromSelection();

		if (player == null)
			return InteractionResult.FAIL;

		if (player.isShiftKeyDown() && stack.has(AllDataComponents.CLICK_TO_LINK_DATA)) {
			if (level.isClientSide)
				return InteractionResult.SUCCESS;
			player.displayClientMessage(CreateLang.translateDirect(msgKey + ".clear"), true);
			stack.remove(AllDataComponents.CLICK_TO_LINK_DATA);
			stack.remove(DataComponents.BLOCK_ENTITY_DATA);
			return InteractionResult.SUCCESS;
		}

		ResourceLocation placedDim = level.dimension()
			.location();

		if (!stack.has(AllDataComponents.CLICK_TO_LINK_DATA)) {
			if (!isValidTarget(level, pos)) {
				if (placeWhenInvalid()) {
					InteractionResult useOn = super.useOn(pContext);
					if (level.isClientSide || useOn == InteractionResult.FAIL)
						return useOn;

					ItemStack itemInHand = player.getItemInHand(pContext.getHand());
					if (!itemInHand.isEmpty()) {
						stack.remove(AllDataComponents.CLICK_TO_LINK_DATA);
						stack.remove(DataComponents.BLOCK_ENTITY_DATA);
					}
					return useOn;
				}

				if (level.isClientSide)
					AllSoundEvents.DENY.playFrom(player);
				player.displayClientMessage(CreateLang.translateDirect(msgKey + ".invalid"), true);
				return InteractionResult.FAIL;
			}

			if (level.isClientSide)
				return InteractionResult.SUCCESS;

			player.displayClientMessage(CreateLang.translateDirect(msgKey + ".set"), true);
			stack.set(AllDataComponents.CLICK_TO_LINK_DATA, new ClickToLinkData(pos, placedDim));
			return InteractionResult.SUCCESS;
		}

		ClickToLinkData data = stack.get(AllDataComponents.CLICK_TO_LINK_DATA);
		//noinspection DataFlowIssue
		BlockPos selectedPos = data.selectedPos();
		ResourceLocation selectedDim = data.selectedDim();
		BlockPos placedPos = pos.relative(pContext.getClickedFace(), state.canBeReplaced() ? 0 : 1);

		if (maxDistance != -1 && (!selectedPos.closerThan(placedPos, maxDistance) || !selectedDim.equals(placedDim))) {
			player.displayClientMessage(CreateLang.translateDirect(msgKey + ".too_far")
				.withStyle(ChatFormatting.RED), true);
			return InteractionResult.FAIL;
		}

		CompoundTag beTag = new CompoundTag();
		beTag.put("TargetOffset", NbtUtils.writeBlockPos(selectedPos.subtract(placedPos)));
		NBTHelper.writeResourceLocation(beTag, "TargetDimension", selectedDim);
		BlockEntity.addEntityType(beTag, ((IBE<?>) this.getBlock()).getBlockEntityType());
		stack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(beTag));

		InteractionResult useOn = super.useOn(pContext);
		if (level.isClientSide || useOn == InteractionResult.FAIL)
			return useOn;

		ItemStack itemInHand = player.getItemInHand(pContext.getHand());
		if (!itemInHand.isEmpty()) {
			stack.remove(AllDataComponents.CLICK_TO_LINK_DATA);
			stack.remove(DataComponents.BLOCK_ENTITY_DATA);
		}
		player.displayClientMessage(CreateLang.translateDirect(msgKey + ".success")
			.withStyle(ChatFormatting.GREEN), true);
		return useOn;
	}

	public abstract int getMaxDistanceFromSelection();

	public abstract String getMessageTranslationKey();

	public boolean placeWhenInvalid() {
		return false;
	}

	public boolean isValidTarget(LevelAccessor level, BlockPos pos) {
		return true;
	}

	public AABB getSelectionBounds(BlockPos pos) {
		Level world = ClientAccess.level();
		BlockState state = world.getBlockState(pos);
		VoxelShape shape = state.getShape(world, pos);
		return shape.isEmpty() ? new AABB(BlockPos.ZERO)
			: shape.bounds()
				.move(pos);
	}

	// fabric: lazily-loaded so the enclosing class verifies on a dedicated server;
	// only ever invoked from client paths
	@Environment(EnvType.CLIENT)
	static class ClientAccess {
		static Level level() {
			return Minecraft.getInstance().level;
		}
	}

	public record ClickToLinkData(BlockPos selectedPos, ResourceLocation selectedDim) {
		public static final Codec<ClickToLinkData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			BlockPos.CODEC.fieldOf("selected_pos").forGetter(ClickToLinkData::selectedPos),
			ResourceLocation.CODEC.fieldOf("selected_dim").forGetter(ClickToLinkData::selectedDim)
		).apply(instance, ClickToLinkData::new));

		public static final StreamCodec<ByteBuf, ClickToLinkData> STREAM_CODEC = StreamCodec.composite(
		    BlockPos.STREAM_CODEC, ClickToLinkData::selectedPos,
		    ResourceLocation.STREAM_CODEC, ClickToLinkData::selectedDim,
		    ClickToLinkData::new
		);
	}
}
