package com.simibubi.create.infrastructure.data;

import java.util.Map.Entry;
import java.util.function.BiConsumer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.simibubi.create.AllKeys;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.Create;
import com.simibubi.create.compat.archEx.ArchExCompat;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.data.DamageTypeTagGen;
import com.simibubi.create.foundation.data.TagLangGen;
import com.simibubi.create.foundation.data.recipe.CreateMechanicalCraftingRecipeGen;
import com.simibubi.create.foundation.data.recipe.CreateRecipeProvider;
import com.simibubi.create.foundation.data.recipe.CreateSequencedAssemblyRecipeGen;
import com.simibubi.create.foundation.data.recipe.CreateStandardRecipeGen;

import com.simibubi.create.foundation.ponder.CreatePonderPlugin;
import com.simibubi.create.foundation.utility.FilesHelper;
import com.simibubi.create.api.registry.CreateRegistries;
import com.tterrag.registrate.providers.ProviderType;

import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;

import io.github.fabricators_of_create.porting_lib.data.ExistingFileHelper;

public class CreateDatagen implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
		ExistingFileHelper helper = ExistingFileHelper.withResourcesFromArg();
		FabricDataGenerator.Pack pack = generator.createPack();
		// fabric: registrate locks data generators once the root generator is built
		addExtraRegistrateData();
		TagLangGen.datagen();
		// fabric: archex compat (addRawLang registers a lazy LANG generator)
		ArchExCompat.init(pack);
		Create.registrate().setupDatagen(pack, helper);
		gatherData(pack, helper);
	}

	public static void gatherData(FabricDataGenerator.Pack pack, ExistingFileHelper existingFileHelper) {

		// fabric: provider lookups get generated entries via buildRegistry()

		pack.addProvider((output, registries) -> AllSoundEvents.provider(output));
		pack.addProvider(GeneratedEntriesProvider::new);
		// fabric: RegistriesDatapackGenerator only dumps RegistryDataLoader.WORLDGEN_REGISTRIES;
		// our DynamicRegistries-registered keys need FabricDynamicRegistryProvider to be written.
		pack.addProvider((output, registries) -> new FabricDynamicRegistryProvider(output, registries) {
			@Override
			protected void configure(HolderLookup.Provider registries, Entries entries) {
				entries.addAll(registries.lookupOrThrow(CreateRegistries.POTATO_PROJECTILE_TYPE));
			}

			@Override
			public String getName() {
				return "Create's Potato Projectile Types";
			}
		});
		pack.addProvider(CreateRecipeSerializerTagsProvider::new);
		pack.addProvider(CreateContraptionTypeTagsProvider::new);
		pack.addProvider(CreateMountedItemStorageTypeTagsProvider::new);
		pack.addProvider(DamageTypeTagGen::new);
		pack.addProvider(AllAdvancements::new);
		pack.addProvider(CreateStandardRecipeGen::new);
		pack.addProvider(CreateMechanicalCraftingRecipeGen::new);
		pack.addProvider(CreateSequencedAssemblyRecipeGen::new);
		pack.addProvider(VanillaHatOffsetGenerator::new);
		pack.addProvider((output, registries) -> new CreateEnchantmentTagsProvider(output, registries, existingFileHelper));
		pack.addProvider((output, registries) -> new CreateWikiBlockInfoProvider(output));

		CreateRecipeProvider.registerAllProcessing(pack);
	}

	@Override
	public void buildRegistry(RegistrySetBuilder registryBuilder) {
		GeneratedEntriesProvider.addBootstraps(registryBuilder);
	}

	private static void addExtraRegistrateData() {
		CreateRegistrateTags.addGenerators();

		Create.registrate().addDataGenerator(ProviderType.LANG, provider -> {
			BiConsumer<String, String> langConsumer = provider::add;

			provideDefaultLang("interface", langConsumer);
			provideDefaultLang("tooltips", langConsumer);
			AllAdvancements.provideLang(langConsumer);
			AllSoundEvents.provideLang(langConsumer);
			AllKeys.provideLang(langConsumer);
			providePonderLang(langConsumer);
			new TagLangGenerator(langConsumer).generate();
		});
	}

	private static void provideDefaultLang(String fileName, BiConsumer<String, String> consumer) {
		String path = "assets/create/lang/default/" + fileName + ".json";
		JsonElement jsonElement = FilesHelper.loadJsonResource(path);
		if (jsonElement == null) {
			throw new IllegalStateException(String.format("Could not find default lang file: %s", path));
		}
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue().getAsString();
			consumer.accept(key, value);
		}
	}

	private static void providePonderLang(BiConsumer<String, String> consumer) {
		// Register this since FMLClientSetupEvent does not run during datagen
		PonderIndex.addPlugin(new CreatePonderPlugin());

		PonderIndex.getLangAccess().provideLang(Create.ID, consumer);
	}
}
