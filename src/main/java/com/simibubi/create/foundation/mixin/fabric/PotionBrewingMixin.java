package com.simibubi.create.foundation.mixin.fabric;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.Ingredient;

// fabric: milk-lib's brewing mixins target pre-1.21 PotionBrewing members (field_8957,
// field_8959, method_8080/registerPotionType, method_8071/registerItemRecipe) which no
// longer exist. Provide them under their literal intermediary names so milk's shadows
// and inject targets resolve. The methods translate calls into the 1.21.1 system via
// FAPI's brewing build callback; milk's own handlers only divert milk-bottle calls into
// the dead list fields, which is harmless. Must apply before milk's mixin (priority 500).
@Mixin(value = PotionBrewing.class, priority = 500)
public abstract class PotionBrewingMixin {
	@Unique
	private static final List<Ingredient> field_8957 = new ArrayList<>();

	@Unique
	private static final List<PotionBrewing.Mix<Item>> field_8959 = new ArrayList<>();

	@Unique
	private static final AtomicBoolean create$callbackRegistered = new AtomicBoolean();

	@Unique
	private static final List<Item> create$pendingContainers = new ArrayList<>();

	@Unique
	private static final List<Object[]> create$pendingItemRecipes = new ArrayList<>();

	@Unique
	private static void create$ensureCallback() {
		if (create$callbackRegistered.compareAndSet(false, true))
			FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {
				for (Item item : create$pendingContainers)
					builder.addContainer(item);
				for (Object[] recipe : create$pendingItemRecipes)
					builder.addContainerRecipe((Item) recipe[0], (Item) recipe[1], (Item) recipe[2]);
			});
	}

	@Unique
	private static void method_8080(Item item) {
		create$ensureCallback();
		create$pendingContainers.add(item);
	}

	@Unique
	private static void method_8071(Item from, Item ingredient, Item to) {
		create$ensureCallback();
		create$pendingItemRecipes.add(new Object[] { from, ingredient, to });
	}
}
