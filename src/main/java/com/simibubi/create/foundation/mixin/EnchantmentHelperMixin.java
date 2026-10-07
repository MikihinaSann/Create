package com.simibubi.create.foundation.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.simibubi.create.foundation.item.CustomEnchantableItem;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {

	@Inject(method = "getItemEnchantmentLevel", at = @At("RETURN"), cancellable = true)
	private static void create$intrinsicEnchantmentLevel(Holder<Enchantment> enchantment, ItemStack stack,
			CallbackInfoReturnable<Integer> cir) {
		if (stack.getItem() instanceof CustomEnchantableItem item)
			cir.setReturnValue(Math.max(cir.getReturnValue(), item.getEnchantmentLevel(stack, enchantment)));
	}
}
