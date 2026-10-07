package com.simibubi.create.foundation.item;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

/**
 * Fabric-side equivalent of Forge's enchantment related IItemExtension methods.
 * <p>
 * {@link #supportsEnchantment} is consulted by mixin hooks into
 * {@link Enchantment#isSupportedItem} and {@link Enchantment#canEnchant} and
 * controls whether an enchantment can be applied to the stack, both at the
 * enchanting table and at an anvil. Implementations that want default behaviour
 * should check {@code enchantment.value().getSupportedItems().contains(stack.getItemHolder())}
 * directly instead of calling back into Enchantment, which would recurse into the mixin.
 * <p>
 * {@link #getEnchantmentLevel} reports an intrinsic enchantment level that is
 * honoured by {@link net.minecraft.world.item.enchantment.EnchantmentHelper#getItemEnchantmentLevel},
 * and {@link #getAllEnchantments} allows items to report intrinsic enchantments
 * that don't live on the stack's data components (tooltips, smithing, etc).
 */
public interface CustomEnchantableItem {

	boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment);

	default int getEnchantmentLevel(ItemStack stack, Holder<Enchantment> enchantment) {
		return 0;
	}

	default ItemEnchantments getAllEnchantments(ItemStack stack, HolderLookup<Enchantment> lookup, ItemEnchantments base) {
		return base;
	}
}
