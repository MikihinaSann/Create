package com.simibubi.create.content.kinetics.chainConveyor;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import net.fabricmc.fabric.api.entity.FakePlayer;

public class ChainConveyorConnectionHandler {

	public static InteractionResult onItemUsedOnBlock(Player player, Level level, InteractionHand hand, BlockHitResult hit) {
		ItemStack itemStack = player.getItemInHand(hand);
		BlockPos pos = hit.getBlockPos();
		BlockState blockState = level.getBlockState(pos);

		if (!AllBlocks.CHAIN_CONVEYOR.has(blockState))
			return InteractionResult.PASS;
		if (!isChain(itemStack))
			return InteractionResult.PASS;
		if (!player.mayBuild() || player instanceof FakePlayer)
			return InteractionResult.PASS;

		if (!level.isClientSide())
			return InteractionResult.CONSUME;
		if (level.getBlockEntity(pos) instanceof ChainConveyorBlockEntity ccbe
			&& ccbe.connections.size() >= AllConfigs.server().kinetics.maxChainConveyorConnections.get()) {
			CreateLang.translate("chain_conveyor.cannot_add_more_connections")
				.style(ChatFormatting.RED)
				.sendStatus(player);
			return InteractionResult.CONSUME;
		}

		// fabric: the selection state lives in ChainConveyorConnectionHandlerClient; these
		// references only resolve on the client since the server returns above
		if (ChainConveyorConnectionHandlerClient.firstPos == null
			|| ChainConveyorConnectionHandlerClient.firstDim != level.dimension()) {
			ChainConveyorConnectionHandlerClient.firstPos = pos;
			ChainConveyorConnectionHandlerClient.firstDim = level.dimension();
			player.swing(hand);
			return InteractionResult.CONSUME;
		}

		boolean success =
			ChainConveyorConnectionHandlerClient.validateAndConnect(level, pos, player, itemStack, false);
		ChainConveyorConnectionHandlerClient.firstPos = null;

		if (!success) {
			AllSoundEvents.DENY.play(level, player, pos);
			return InteractionResult.CONSUME;
		}

		SoundType soundtype = Blocks.CHAIN.defaultBlockState()
			.getSoundType();
		if (soundtype != null)
			level.playSound(player, pos, soundtype.getPlaceSound(), SoundSource.BLOCKS,
				(soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);

		return InteractionResult.CONSUME;
	}

	static boolean isChain(ItemStack itemStack) {
		return itemStack.is(Items.CHAIN); // Replace with tag? generic renderer?
	}

}
