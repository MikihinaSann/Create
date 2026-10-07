package com.simibubi.create.content.redstone.displayLink;

import com.simibubi.create.AllDataComponents;

import net.createmod.catnip.outliner.Outliner;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

// fabric: client-only counterpart of ClickToLinkBlockItem, kept in a separate class so
// the common item class verifies cleanly on a dedicated server
@Environment(EnvType.CLIENT)
public class ClickToLinkBlockItemClient {

	private static BlockPos lastShownPos = null;
	private static AABB lastShownAABB = null;

	public static void clientTick() {
		Player player = Minecraft.getInstance().player;
		if (player == null)
			return;
		ItemStack heldItemMainhand = player.getMainHandItem();
		if (!(heldItemMainhand.getItem() instanceof ClickToLinkBlockItem blockItem))
			return;
		if (!heldItemMainhand.has(AllDataComponents.CLICK_TO_LINK_DATA))
			return;

		//noinspection DataFlowIssue
		BlockPos selectedPos = heldItemMainhand.get(AllDataComponents.CLICK_TO_LINK_DATA).selectedPos();

		if (!selectedPos.equals(lastShownPos)) {
			lastShownAABB = blockItem.getSelectionBounds(selectedPos);
			lastShownPos = selectedPos;
		}

		Outliner.getInstance().showAABB("target", lastShownAABB)
			.colored(0xffcb74)
			.lineWidth(1 / 16f);
	}

}
