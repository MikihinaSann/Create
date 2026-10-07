package com.simibubi.create.infrastructure.fabric.transfer;

import com.simibubi.create.infrastructure.fabric.transfer.fluid.FluidStack;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.minecraft.world.Container;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalLong;
import java.util.function.Function;
import java.util.function.Predicate;

public class TransferUtil {
	public static int truncateLong(long l) {
		return (int) Math.min(l, Integer.MAX_VALUE);
	}

	/**
	 * Returns the fluid contained in an item's fluid storage, if any.
	 */
	public static java.util.Optional<FluidStack> getFluidContained(ItemStack stack) {
		Storage<FluidVariant> storage = net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext.withConstant(stack)
			.find(FluidStorage.ITEM);
		if (storage == null)
			return java.util.Optional.empty();
		for (StorageView<FluidVariant> view : storage.nonEmptyViews()) {
			if (!view.isResourceBlank() && view.getAmount() > 0)
				return java.util.Optional.of(new FluidStack(view.getResource(), view.getAmount()));
		}
		return java.util.Optional.empty();
	}

	public static long insert(Storage<FluidVariant> storage, FluidStack stack) {
		try (Transaction t = Transaction.openOuter()) {
			long inserted = insert(storage, stack, t);
			t.commit();
			return inserted;
		}
	}

	public static long insert(Storage<ItemVariant> storage, ItemStack stack) {
		try (Transaction t = Transaction.openOuter()) {
			long inserted = insert(storage, stack, t);
			t.commit();
			return inserted;
		}
	}

	public static long insert(Container container, ItemStack stack) {
		return insert(InventoryStorage.of(container, null), stack);
	}

	public static long insert(Storage<FluidVariant> storage, FluidStack stack, TransactionContext ctx) {
		return storage.insert(stack.getVariant(), stack.getAmount(), ctx);
	}

	public static long insert(Storage<ItemVariant> storage, ItemStack stack, TransactionContext ctx) {
		return storage.insert(ItemVariant.of(stack), stack.getCount(), ctx);
	}

	@Nullable
	public static <T extends TransferVariant<?>> ResourceAmount<T> extractAny(Storage<T> storage, long maxAmount) {
		return commit(t -> StorageUtil.extractAny(storage, maxAmount, t));
	}

	public static <T extends TransferVariant<?>> long extract(Storage<T> storage, T resource, long maxAmount) {
		return commit(t -> {
			long extracted = 0;
			for (StorageView<T> view : storage) {
				if (!view.getResource().equals(resource))
					continue;
				extracted += view.extract(resource, maxAmount - extracted, t);
				if (extracted >= maxAmount)
					break;
			}
			return extracted;
		});
	}

	@Nullable
	public static <T extends TransferVariant<?>> ResourceAmount<T> extractMatching(Storage<T> storage, Predicate<T> predicate, long maxAmount, TransactionContext ctx) {
		T resourceExtracting = null;
		long extracted = 0;

		for (StorageView<T> view : storage.nonEmptyViews()) {
			T resource = view.getResource();

			// see if a resource has already been chosen
			if (resourceExtracting != null && !resourceExtracting.equals(resource))
				continue;

			// if one hasn't, see if this one matches
			if (resourceExtracting == null && predicate.test(resource)) {
				resourceExtracting = resource;
			} else {
				// nope, skip
				continue;
			}

			extracted += view.extract(resource, maxAmount - extracted, ctx);
			if (extracted >= maxAmount) {
				return new ResourceAmount<>(resource, extracted);
			}
		}

		return resourceExtracting != null ? new ResourceAmount<>(resourceExtracting, extracted) : null;
	}

	@Nullable
	public static Storage<ItemVariant> getItemStorage(BlockEntity be) {
		return ItemStorage.SIDED.find(be.getLevel(), be.getBlockPos(), be.getBlockState(), be, null);
	}

	@Nullable
	public static Storage<ItemVariant> getItemStorage(Level level, BlockPos pos) {
		return ItemStorage.SIDED.find(level, pos, null);
	}

	@Nullable
	public static Storage<ItemVariant> getItemStorage(Level level, BlockPos pos, @Nullable Direction direction) {
		return ItemStorage.SIDED.find(level, pos, direction);
	}

	@Nullable
	public static Storage<FluidVariant> getFluidStorage(Level level, BlockPos pos) {
		return FluidStorage.SIDED.find(level, pos, null);
	}

	@Nullable
	public static Storage<FluidVariant> getFluidStorage(Level level, BlockPos pos, @Nullable Direction direction) {
		return FluidStorage.SIDED.find(level, pos, direction);
	}

	@Nullable
	public static Storage<FluidVariant> getFluidStorage(Level level, BlockPos pos, @Nullable BlockEntity be,
		@Nullable Direction direction) {
		BlockState state = be == null ? null : be.getBlockState();
		return FluidStorage.SIDED.find(level, pos, state, be, direction);
	}

	public static FluidStack extractAnyFluid(Storage<FluidVariant> storage, long maxAmount) {
		ResourceAmount<FluidVariant> extracted = extractAny(storage, maxAmount);
		return extracted == null ? FluidStack.EMPTY : new FluidStack(extracted.resource(), extracted.amount());
	}

	public static List<ItemStack> getAllItems(Storage<ItemVariant> storage) {
		List<ItemStack> items = new ArrayList<>();
		for (StorageView<ItemVariant> view : storage.nonEmptyViews())
			items.add(view.getResource().toStack((int) Math.min(view.getAmount(), Integer.MAX_VALUE)));
		return items;
	}

	public static List<ItemStack> extractAllAsStacks(Storage<ItemVariant> storage) {
		List<ItemStack> extracted = new ArrayList<>();
		try (Transaction t = Transaction.openOuter()) {
			for (StorageView<ItemVariant> view : storage.nonEmptyViews()) {
				long amount = view.extract(view.getResource(), Long.MAX_VALUE, t);
				long remaining = amount;
				while (remaining > 0) {
					ItemStack stack = view.getResource().toStack((int) Math.min(remaining, view.getResource().getItem().getDefaultMaxStackSize()));
					remaining -= stack.getCount();
					extracted.add(stack);
				}
			}
			t.commit();
		}
		return extracted;
	}

	public static FluidStack firstOrEmpty(Storage<FluidVariant> storage) {
		for (StorageView<FluidVariant> view : storage.nonEmptyViews())
			return new FluidStack(view.getResource(), view.getAmount());
		return FluidStack.EMPTY;
	}

	public static long totalCapacity(Storage<?> storage) {
		long total = 0;
		for (StorageView<?> view : storage)
			total += view.getCapacity();
		return total;
	}

	public static OptionalLong firstCapacity(Storage<?> storage) {
		for (StorageView<?> view : storage) {
			return OptionalLong.of(view.getCapacity());
		}
		return OptionalLong.empty();
	}

	public static <T> void clear(Storage<T> storage) {
		try (Transaction t = Transaction.openOuter()) {
			for (StorageView<T> view : storage.nonEmptyViews()) {
				view.extract(view.getResource(), view.getAmount(), t);
			}
			t.commit();
		}
	}

	public static <T> T commit(Function<TransactionContext, T> function) {
		try (Transaction t = Transaction.openOuter()) {
			T value = function.apply(t);
			t.commit();
			return value;
		}
	}

	public static <T> T simulate(Function<TransactionContext, T> function) {
		try (Transaction t = Transaction.openOuter()) {
			return function.apply(t);
		}
	}

	/**
	 * Replacement for the removed {@code Storage#exactView}: returns a view whose
	 * resource exactly matches the given resource, or null if none does.
	 */
	@Nullable
	public static <T> StorageView<T> exactView(Storage<T> storage, T resource) {
		for (StorageView<T> view : storage) {
			if (view.getResource().equals(resource))
				return view;
		}
		return null;
	}
}
