package com.simibubi.create.foundation.item;

import net.minecraft.world.item.ItemStack;

/**
 * Items implementing this can control their max stack size per-stack.
 * Hooked into {@link ItemStack#getMaxStackSize()} by ItemStackMixin.
 */
public interface CustomMaxCountItem {
	int getItemStackLimit(ItemStack stack);
}
