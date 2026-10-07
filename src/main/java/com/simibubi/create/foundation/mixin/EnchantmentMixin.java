package com.simibubi.create.foundation.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.simibubi.create.foundation.item.CustomEnchantableItem;
import com.simibubi.create.foundation.utility.EnchantmentLookup;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

@Mixin(Enchantment.class)
public abstract class EnchantmentMixin {

	@Inject(method = "isSupportedItem", at = @At("HEAD"), cancellable = true)
	private void create$customSupportedItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
		create$customCheck(stack, cir);
	}

	@Inject(method = "canEnchant", at = @At("HEAD"), cancellable = true)
	private void create$customCanEnchant(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
		create$customCheck(stack, cir);
	}

	private void create$customCheck(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
		if (!(stack.getItem() instanceof CustomEnchantableItem item))
			return;
		Enchantment enchantment = (Enchantment) (Object) this;
		Holder<Enchantment> holder = EnchantmentLookup.holderOf(enchantment).orElse(null);
		if (holder == null)
			return;
		cir.setReturnValue(item.supportsEnchantment(stack, holder));
	}
}
