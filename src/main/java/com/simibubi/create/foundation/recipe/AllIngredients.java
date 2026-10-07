package com.simibubi.create.foundation.recipe;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.simibubi.create.foundation.item.BlockTagIngredient;

import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;

public class AllIngredients {

	// fabric: CustomIngredientSerializer instead of neoforge's ingredient-type registry

	@Internal
	public static void register() {
		CustomIngredientSerializer.register(BlockTagIngredient.SERIALIZER);
	}
}
