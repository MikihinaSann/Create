package com.simibubi.create.foundation.data.recipe;

import org.jetbrains.annotations.Nullable;

import com.google.common.base.Preconditions;

import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.impl.datagen.FabricDataGenHelper;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

/**
 * Equivalent of NeoForge's conditional RecipeOutput. Attaches
 * {@code fabric:load_conditions} to generated recipes; the conditions are
 * picked up by {@link FabricRecipeProvider}'s exporter during datagen.
 */
public record ConditionalRecipeOutput(RecipeOutput wrapped, ResourceCondition[] conditions) implements RecipeOutput {

	public static RecipeOutput wrap(RecipeOutput output, ResourceCondition... conditions) {
		Preconditions.checkArgument(conditions.length > 0, "Must add at least one condition.");
		return new ConditionalRecipeOutput(output, conditions);
	}

	@Override
	public Advancement.Builder advancement() {
		return wrapped.advancement();
	}

	@Override
	public void accept(ResourceLocation id, Recipe<?> recipe, @Nullable AdvancementHolder advancement) {
		FabricDataGenHelper.addConditions(recipe, conditions);
		wrapped.accept(id, recipe, advancement);
	}
}
