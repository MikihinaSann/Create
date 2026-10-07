package com.simibubi.create.foundation.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import com.mojang.datafixers.util.Either;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import com.simibubi.create.foundation.data.SimpleDatagenIngredient;

import net.minecraft.world.item.crafting.Ingredient;

import net.minecraft.world.item.crafting.Ingredient.Value;

import net.fabricmc.fabric.impl.datagen.FabricDataGenHelper;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Ingredient.Value.class)
public interface Ingredient$ValueMixin {
	@ModifyExpressionValue(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/Codec;xmap(Ljava/util/function/Function;Ljava/util/function/Function;)Lcom/mojang/serialization/Codec;"))
	private static Codec<Value> create$recipeWithoutCompound(Codec<Value> original) {
		if (!FabricDataGenHelper.ENABLED) {
			return original;
		}
		return Codec.either(SimpleDatagenIngredient.MAP_CODEC.codec(), original).xmap((thing) -> thing.map(dv -> dv, v -> v), value -> {
			if (value instanceof SimpleDatagenIngredient datagenValue) {
				return Either.left(datagenValue);
			}
			return Either.right(value);
		});
	}
}
