package com.simibubi.create.foundation.mixin.fabric;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

// fabric: porting-lib beta.53's EffectRenderingInventoryScreenMixin leaves its
// renderCustomInventoryText body TODO'd out, so the @Share("custom") LocalRef is
// never populated and cancelInventoryText NPEs unboxing it on every container
// screen render. Upstream fixed this in commit 6a119667 (beta.66+); default the
// unset ref to false (don't cancel) until the dependency is bumped.
@Mixin(targets = "io.github.fabricators_of_create.porting_lib.entity.mixin.client.EffectRenderingInventoryScreenMixin", remap = false)
public abstract class EffectRenderingInventoryScreenFixMixin {
	@ModifyExpressionValue(method = "cancelInventoryText", at = @At(value = "INVOKE", target = "Lcom/llamalad7/mixinextras/sugar/ref/LocalRef;get()Ljava/lang/Object;"))
	private Object create$defaultUncancelled(Object original) {
		return original == null ? Boolean.FALSE : original;
	}
}
