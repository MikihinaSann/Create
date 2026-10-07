package com.simibubi.create.foundation.recipe;

import org.jetbrains.annotations.NotNull;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;

public class DummyCraftingContainer extends TransientCraftingContainer {

	private final NonNullList<ItemStack> inv;

	public DummyCraftingContainer(NonNullList<ItemStack> stacks) {
		super(null, 0, 0);
		this.inv = stacks;
	}

	// fabric: snapshot a Storage<ItemVariant> (e.g. mid-transaction after simulated extraction)
	public DummyCraftingContainer(Storage<ItemVariant> storage) {
		super(null, 0, 0);
		NonNullList<ItemStack> stacks = NonNullList.create();
		for (StorageView<ItemVariant> view : storage.nonEmptyViews())
			stacks.add(view.getResource().toStack());
		this.inv = stacks;
	}

	@Override
	public int getContainerSize() {
		return this.inv.size();
	}

	@Override
	public boolean isEmpty() {
		for (int slot = 0; slot < this.getContainerSize(); slot++) {
			if (!this.getItem(slot).isEmpty())
				return false;
		}

		return true;
	}

	@Override
	public @NotNull ItemStack getItem(int slot) {
		return slot >= this.getContainerSize() ? ItemStack.EMPTY : this.inv.get(slot);
	}

	@Override
	public @NotNull ItemStack removeItemNoUpdate(int slot) {
		return ItemStack.EMPTY;
	}

	@Override
	public @NotNull ItemStack removeItem(int slot, int count) {
		return ItemStack.EMPTY;
	}

	@Override
	public void setItem(int slot, @NotNull ItemStack stack) {}

	@Override
	public void clearContent() {}

	@Override
	public void fillStackedContents(@NotNull StackedContents helper) {}
}
