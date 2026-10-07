package com.simibubi.create.content.equipment.clipboard;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.trains.track.TrackBlockOutline;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.utility.CreateLang;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

// fabric: client-only counterparts of ClipboardValueSettingsHandler, kept in a separate
// class so the common handler verifies cleanly on a dedicated server
@Environment(EnvType.CLIENT)
public class ClipboardValueSettingsHandlerClient {

	public static boolean drawCustomBlockSelection(LevelRenderer context, Camera camera, HitResult hitResult, DeltaTracker deltaTracker, PoseStack ms, MultiBufferSource buffers) {
		Minecraft mc = Minecraft.getInstance();
		BlockHitResult target = (BlockHitResult) hitResult;
		BlockPos pos = target.getBlockPos();
		BlockState blockstate = mc.level.getBlockState(pos);

		if (mc.player == null || mc.player.isSpectator())
			return false;
		if (!mc.level.getWorldBorder()
			.isWithinBounds(pos))
			return false;
		if (!AllBlocks.CLIPBOARD.isIn(mc.player.getMainHandItem()))
			return false;
		if (!(mc.level.getBlockEntity(pos) instanceof SmartBlockEntity smartBE))
			return false;
		if (!(smartBE instanceof ClipboardBlockEntity) && !smartBE.getAllBehaviours()
			.stream()
			.noneMatch(b -> b instanceof ClipboardCloneable cc
				&& cc.writeToClipboard(mc.level.registryAccess(), new CompoundTag(), target.getDirection()))
			&& !(smartBE instanceof ClipboardCloneable))
			return false;

		VoxelShape shape = blockstate.getShape(mc.level, pos);
		if (shape.isEmpty())
			return false;

		VertexConsumer vb = buffers
			.getBuffer(RenderType.lines());
		Vec3 camPos = camera
			.getPosition();

		ms.pushPose();
		ms.translate(pos.getX() - camPos.x, pos.getY() - camPos.y, pos.getZ() - camPos.z);
		TrackBlockOutline.renderShape(shape, ms, vb, true);

		ms.popPose();
		return true;
	}

	public static void clientTick() {
		Minecraft mc = Minecraft.getInstance();
		if (!(mc.hitResult instanceof BlockHitResult target))
			return;
		Player player = mc.player;
		if (!AllBlocks.CLIPBOARD.isIn(player.getMainHandItem()))
			return;
		BlockPos pos = target.getBlockPos();
		if (!(mc.level.getBlockEntity(pos) instanceof SmartBlockEntity smartBE))
			return;

		if (smartBE instanceof ClipboardBlockEntity) {
			List<MutableComponent> tip = new ArrayList<>();
			tip.add(CreateLang.translateDirect("clipboard.actions"));
			tip.add(CreateLang.translateDirect("clipboard.copy_other_clipboard", Component.keybind("key.use")));
			CreateClient.VALUE_SETTINGS_HANDLER.showHoverTip(tip);
			return;
		}

		ClipboardContent content = mc.player.getMainHandItem()
			.get(AllDataComponents.CLIPBOARD_CONTENT);
		if (content == null)
			return;

		CompoundTag tagElement = content.copiedValues().orElse(null);

		boolean canCopy = smartBE.getAllBehaviours()
			.stream()
			.anyMatch(b -> b instanceof ClipboardCloneable cc
				&& cc.writeToClipboard(mc.level.registryAccess(), new CompoundTag(), target.getDirection()))
			|| smartBE instanceof ClipboardCloneable ccbe
				&& ccbe.writeToClipboard(mc.level.registryAccess(), new CompoundTag(), target.getDirection());

		boolean canPaste = tagElement != null && (smartBE.getAllBehaviours()
			.stream()
			.anyMatch(b -> b instanceof ClipboardCloneable cc && cc.readFromClipboard(mc.level.registryAccess(),
				tagElement.getCompound(cc.getClipboardKey()), mc.player, target.getDirection(), true))
			|| smartBE instanceof ClipboardCloneable ccbe && ccbe.readFromClipboard(mc.level.registryAccess(),
				tagElement.getCompound(ccbe.getClipboardKey()), mc.player, target.getDirection(), true));

		if (!canCopy && !canPaste)
			return;

		List<MutableComponent> tip = new ArrayList<>();
		tip.add(CreateLang.translateDirect("clipboard.actions"));
		if (canCopy)
			tip.add(CreateLang.translateDirect("clipboard.to_copy", Component.keybind("key.use")));
		if (canPaste)
			tip.add(CreateLang.translateDirect("clipboard.to_paste", Component.keybind("key.attack")));

		CreateClient.VALUE_SETTINGS_HANDLER.showHoverTip(tip);
	}
}
