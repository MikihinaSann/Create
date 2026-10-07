package com.simibubi.create.compat.rei.display;

import java.util.ArrayList;
import java.util.List;

import com.simibubi.create.Create;
import com.simibubi.create.foundation.fluid.SizedFluidIngredient;
import com.simibubi.create.compat.rei.category.CreateRecipeCategory;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.foundation.fluid.FluidIngredient;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.util.EntryIngredients;

public class BasinDisplay extends CreateDisplay<BasinRecipe> {
	public BasinDisplay(BasinRecipe recipe, CategoryIdentifier<CreateDisplay<BasinRecipe>> id) {
		super(recipe, id,
				getInputs(recipe),
				List.of(
						EntryIngredients.ofItemStacks(recipe.getRollableResultsAsItemStacks()),
						EntryIngredients.of(VanillaEntryTypes.FLUID, CreateRecipeCategory.convertToREIFluids(recipe.getFluidResults()))
				));
	}

	public static BasinDisplay mixing(BasinRecipe recipe) {
		return new BasinDisplay(recipe, CategoryIdentifier.of(Create.asResource("mixing")));
	}

	public static BasinDisplay autoShapeless(BasinRecipe recipe) {
		return new BasinDisplay(recipe, CategoryIdentifier.of(Create.asResource("automatic_shapeless")));
	}

	public static BasinDisplay brewing(BasinRecipe recipe) {
		return new BasinDisplay(recipe, CategoryIdentifier.of(Create.asResource("automatic_brewing")));
	}

	public static BasinDisplay packing(BasinRecipe recipe) {
		return new BasinDisplay(recipe, CategoryIdentifier.of(Create.asResource("packing")));
	}

	public static BasinDisplay autoSquare(BasinRecipe recipe) {
		return new BasinDisplay(recipe, CategoryIdentifier.of(Create.asResource("automatic_packing")));
	}

	private static List<EntryIngredient> getInputs(BasinRecipe recipe) {
		List<EntryIngredient> input = new ArrayList<>(EntryIngredients.ofIngredients(recipe.getIngredients()));
		for (SizedFluidIngredient fluidIngredient : recipe.getFluidIngredients()) {
			input.add(EntryIngredients.of(VanillaEntryTypes.FLUID, CreateRecipeCategory.convertToREIFluids(fluidIngredient.getFluids())));
		}
		return input;
	}
}
