package com.simibubi.create.content.logistics.tunnel;

import com.simibubi.create.foundation.item.ItemHelper;

import net.minecraft.world.Clearable;
import net.minecraft.world.item.ItemStack;

import net.neoforged.neoforge.items.IItemHandler;

public class BrassTunnelItemHandler implements IItemHandler, Clearable {

	private BrassTunnelBlockEntity blockEntity;

	public BrassTunnelItemHandler(BrassTunnelBlockEntity be) {
		this.blockEntity = be;
	}

	@Override
	public void clearContent() {
		blockEntity.stackToDistribute = ItemStack.EMPTY;
	}

	@Override
	public int getSlots() {
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return blockEntity.stackToDistribute;
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if (!blockEntity.hasDistributionBehaviour()) {
			Storage<ItemVariant> beltCapability = blockEntity.getBeltCapability();
			if (beltCapability == null)
				return 0;
			return beltCapability.insert(resource, maxAmount, transaction);
		}

		if (!blockEntity.canTakeItems())
			return 0;
		int toInsert = Math.min(ItemHelper.truncateLong(maxAmount), resource.getItem().getMaxStackSize());

		blockEntity.setStackToDistribute(resource.toStack(toInsert), null, transaction);
		return toInsert;
	}

	@Override
	public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		Storage<ItemVariant> beltCapability = blockEntity.getBeltCapability();
		if (beltCapability == null)
			return 0;
		return beltCapability.extract(resource, maxAmount, transaction);
	}

	@Override
	public boolean isResourceBlank() {
		return getResource().isBlank();
	}

	@Override
	public ItemVariant getResource() {
		return ItemVariant.of(getStack());
	}

	@Override
	public long getAmount() {
		ItemStack stack = getStack();
		return stack.isEmpty() ? 0 : stack.getCount();
	}

	@Override
	public long getCapacity() {
		return getStack().getMaxStackSize();
	}

	public ItemStack getStack() {
		ItemStack stack = blockEntity.stackToDistribute;
		if (stack.isEmpty())
			return ItemStack.EMPTY;
		return stack;
	}

	@Override
	public int getSlotLimit(int slot) {
		return blockEntity.stackToDistribute.isEmpty() ? 64 : blockEntity.stackToDistribute.getMaxStackSize();
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		return true;
	}

}
