package com.simibubi.create.foundation.blockEntity;

import org.jetbrains.annotations.ApiStatus;

import io.github.fabricators_of_create.porting_lib.transfer.item.SlottedStackStorage;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

/**
 * fabric port: stand-in for NeoForge's RecipeWrapper — adapts a SlottedStackStorage
 * to a {@link RecipeInput} for recipe lookups.
 */
public class RecipeWrapper implements RecipeInput {

	protected final SlottedStackStorage inv;

	public RecipeWrapper(SlottedStackStorage inv) {
		this.inv = inv;
	}

	@Override
	@ApiStatus.NonExtendable
	@ApiStatus.Internal
	public int size() {
		return inv.getSlotCount();
	}

	@Override
	public ItemStack getItem(int slot) {
		return inv.getStackInSlot(slot);
	}

	@Override
	public boolean isEmpty() {
		for (int i = 0; i < inv.getSlotCount(); i++) {
			if (!inv.getStackInSlot(i).isEmpty())
				return false;
		}
		return true;
	}

}
