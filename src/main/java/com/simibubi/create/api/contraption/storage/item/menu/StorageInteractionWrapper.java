package com.simibubi.create.api.contraption.storage.item.menu;

import java.util.function.Consumer;
import com.simibubi.create.infrastructure.fabric.transfer.item.SlottedStackStorage;
import java.util.function.Predicate;

import com.simibubi.create.foundation.blockEntity.ItemHandlerContainer;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;

public class StorageInteractionWrapper extends ItemHandlerContainer {
	private final Predicate<Player> stillValid;
	private final Consumer<Player> onClose;

	public StorageInteractionWrapper(SlottedStackStorage storage, Predicate<Player> stillValid, Consumer<Player> onClose) {
		super(storage);
		this.stillValid = stillValid;
		this.onClose = onClose;
	}

	@Override
	public boolean stillValid(Player player) {
		return this.stillValid.test(player);
	}

	@Override
	public void stopOpen(Player player) {
		this.onClose.accept(player);
	}

	@Override
	public int getContainerSize() {
		return this.inv.getSlotCount();
	}

	@Override
	public boolean isEmpty() {
		return this.inv.nonEmptyIterator().hasNext();
	}

	@Override
	public ItemStack getItem(int slot) {
		return this.inv.getStackInSlot(slot);
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		if (index >= 0 && index < this.inv.getSlotCount()) {
			ItemStack current = this.inv.getStackInSlot(index);
			if (current.isEmpty())
				return ItemStack.EMPTY;
			current = current.copy();
			ItemStack extracted = current.split(count);
			this.inv.setStackInSlot(index, current);
			return extracted;
		}
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		return removeItem(index, Integer.MAX_VALUE);
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		this.inv.setStackInSlot(slot, stack);
	}

	@Override
	public void setChanged() {
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack) {
		return this.inv.isItemValid(index, ItemVariant.of(stack), stack.getCount());
	}

	@Override
	public void clearContent() {
		for (int i = 0; i < this.inv.getSlotCount(); i++) {
			this.inv.setStackInSlot(i, ItemStack.EMPTY);
		}
	}
}
