package com.simibubi.create.foundation.recipe;

import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

public class RecipeHelper {

	// fabric: vanilla Recipe has no getIngredients(); dispatch on the concrete types
	public static NonNullList<Ingredient> getIngredients(Recipe<?> recipe) {
		if (recipe instanceof CraftingRecipe crafting)
			return crafting.getIngredients();
		if (recipe instanceof SequencedAssemblyRecipe assembly)
			return assembly.getIngredients();
		if (recipe instanceof ProcessingRecipe<?, ?> processing)
			return processing.getIngredients();
		return NonNullList.create();
	}
}
