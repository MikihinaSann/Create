package com.simibubi.create.content.equipment.symmetryWand;

import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.utility.AdventureUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import io.github.fabricators_of_create.porting_lib.level.events.BlockEvent.EntityPlaceEvent;

public class SymmetryHandler {

	private static boolean handlingSymmetry = false; // fabric: prevent infinite recursion in break event listening

	public static void onBlockPlaced(EntityPlaceEvent event) {
		if (event.getLevel()
			.isClientSide())
			return;

		if (!(event.getEntity() instanceof Player player))
			return;
		if (AdventureUtil.isAdventure(player))
			return;
		Inventory inv = player.getInventory();
		for (int i = 0; i < Inventory.getSelectionSize(); i++)
			if (AllItems.WAND_OF_SYMMETRY.isIn(inv.getItem(i)))
				SymmetryWandItem.apply(player.level(), inv.getItem(i), player, event.getPos(), event.getPlacedBlock());
	}

	public static boolean onBlockDestroyed(Level world, Player player, BlockPos pos, BlockState state, /* Nullable */ BlockEntity blockEntity) {
		if (handlingSymmetry || AdventureUtil.isAdventure(player))
			return true;

		if (world
			.isClientSide())
			return true;

		if (player.isSpectator())
			return true;

		Inventory inv = player.getInventory();
		handlingSymmetry = true;
		for (int i = 0; i < Inventory.getSelectionSize(); i++)
			if (AllItems.WAND_OF_SYMMETRY.isIn(inv.getItem(i)))
				SymmetryWandItem.remove(player.level(), inv.getItem(i), player, pos);
		handlingSymmetry = false;
		return true;
	}

}
