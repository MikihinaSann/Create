package com.simibubi.create.content.equipment.toolbox;

import java.util.Iterator;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

import net.minecraft.world.Container;

/**
 * For inserting items into a players' inventory anywhere except the hotbar.
 * fabric: delegates to InventoryStorage, restricting insertion to main inventory slots (9-35)
 */
public class ItemReturnInvWrapper implements SlottedStorage<ItemVariant> {

	private final InventoryStorage backing;

	public ItemReturnInvWrapper(Container inv) {
		this.backing = InventoryStorage.of(inv, null);
	}

	@Override
	public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		StoragePreconditions.notBlankNotNegative(resource, maxAmount);
		long inserted = 0;
		List<SingleSlotStorage<ItemVariant>> slots = backing.getSlots();
		// two passes: fill existing stacks first, then empty slots
		for (int pass = 0; pass < 2 && inserted < maxAmount; pass++) {
			for (int i = 9; i <= 35 && i < slots.size(); i++) {
				SingleSlotStorage<ItemVariant> slot = slots.get(i);
				if ((pass == 0) == slot.isResourceBlank())
					continue;
				inserted += slot.insert(resource, maxAmount - inserted, transaction);
				if (inserted >= maxAmount)
					break;
			}
		}
		return inserted;
	}

	@Override
	public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		return backing.extract(resource, maxAmount, transaction);
	}

	@Override
	public int getSlotCount() {
		return backing.getSlotCount();
	}

	@Override
	public SingleSlotStorage<ItemVariant> getSlot(int slot) {
		return backing.getSlot(slot);
	}

	@Override
	public List<SingleSlotStorage<ItemVariant>> getSlots() {
		return backing.getSlots();
	}

	@Override
	public Iterator<StorageView<ItemVariant>> iterator() {
		return backing.iterator();
	}

	@Override
	public @Nullable Iterator<StorageView<ItemVariant>> nonEmptyIterator() {
		return backing.nonEmptyIterator();
	}

	@Override
	public Iterable<StorageView<ItemVariant>> nonEmptyViews() {
		return backing.nonEmptyViews();
	}
}
