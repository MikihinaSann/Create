package com.simibubi.create.infrastructure.data;

import java.util.concurrent.CompletableFuture;

import com.simibubi.create.AllDamageTypes;
import com.simibubi.create.AllEnchantments;
import com.simibubi.create.api.registry.CreateRegistries;
import com.simibubi.create.content.equipment.potatoCannon.AllPotatoProjectileTypes;
import com.simibubi.create.infrastructure.worldgen.AllConfiguredFeatures;
import com.simibubi.create.infrastructure.worldgen.AllPlacedFeatures;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.registries.RegistriesDatapackGenerator;

// fabric: buildRegistry() supplies the patched registries via DataGeneratorEntrypoint
public class GeneratedEntriesProvider extends RegistriesDatapackGenerator {

	public GeneratedEntriesProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	// fabric: this must be reused in the entrypoint, moved to a method
	public static RegistrySetBuilder addBootstraps(RegistrySetBuilder builder) {
		return builder.add(Registries.ENCHANTMENT, AllEnchantments::bootstrap)
			.add(Registries.DAMAGE_TYPE, AllDamageTypes::bootstrap)
			.add(Registries.CONFIGURED_FEATURE, AllConfiguredFeatures::bootstrap)
			.add(Registries.PLACED_FEATURE, AllPlacedFeatures::bootstrap)
			.add(CreateRegistries.POTATO_PROJECTILE_TYPE, AllPotatoProjectileTypes::bootstrap);
		// fabric: biome modifiers are not a registry, remove
	}

	@Override
	public String getName() {
		return "Create's Generated Registry Entries";
	}
}
