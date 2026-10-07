package com.simibubi.create.foundation.blockEntity.behaviour.inventory;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.simibubi.create.foundation.utility.fabric.ListeningStorageView;
import com.simibubi.create.infrastructure.fabric.ProcessingIterator;
import com.simibubi.create.infrastructure.fabric.transfer.TransactionSuccessCallback;
import com.simibubi.create.infrastructure.fabric.transfer.TransferUtil;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

public class VersionedInventoryWrapper implements Storage<ItemVariant> {

	public static final AtomicInteger idGenerator = new AtomicInteger();

	private Storage<ItemVariant> inventory;
	private int version;
	private int id;

	public VersionedInventoryWrapper(Storage<ItemVariant> inventory) {
		this.id = idGenerator.getAndIncrement();
		this.inventory = inventory;
		this.version = 0;
	}

	public void incrementVersion() {
		version++;
	}

	private void listen(TransactionContext transaction) {
		TransactionSuccessCallback.register(transaction, this::incrementVersion);
	}

	public int getId() {
		return id;
	}

	@Override
	public long getVersion() {
		return this.version;
	}


	//

	@Override
	public boolean supportsInsertion() {
		return inventory.supportsInsertion();
	}

	@Override
	public boolean supportsExtraction() {
		return inventory.supportsExtraction();
	}

	//

	@Override
	public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		this.listen(transaction);
		return inventory.insert(resource, maxAmount, transaction);
	}

	public long simulateInsert(ItemVariant resource, long maxAmount, @Nullable TransactionContext transaction) {
		this.listen(transaction);
		return StorageUtil.simulateInsert(inventory, resource, maxAmount, transaction);
	}

	@Override
	public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		this.listen(transaction);
		return inventory.extract(resource, maxAmount, transaction);
	}

	public long simulateExtract(ItemVariant resource, long maxAmount, @Nullable TransactionContext transaction) {
		this.listen(transaction);
		return StorageUtil.simulateExtract(inventory, resource, maxAmount, transaction);
	}

	@Override
	@NotNull
	public Iterator<StorageView<ItemVariant>> iterator() {
		return new ProcessingIterator<>(inventory.iterator(), view -> new ListeningStorageView<>(view, this::incrementVersion));
	}

	@Override
	public Iterator<StorageView<ItemVariant>> nonEmptyIterator() {
		return new ProcessingIterator<>(inventory.nonEmptyIterator(), view -> new ListeningStorageView<>(view, this::incrementVersion));
	}

	@Override
	public Iterable<StorageView<ItemVariant>> nonEmptyViews() {
		return this::nonEmptyIterator;
	}

	@Nullable
	public StorageView<ItemVariant> exactView(ItemVariant resource) {
		return new ListeningStorageView<>(TransferUtil.exactView(inventory, resource), this::incrementVersion);
	}
}
