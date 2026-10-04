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
import com.simibubi.create.foundation.data.CreateDatamapProvider;
import com.simibubi.create.foundation.data.DamageTypeTagGen;
import com.simibubi.create.foundation.data.TagLangGen;
import com.simibubi.create.foundation.data.recipe.CreateMechanicalCraftingRecipeGen;
import com.simibubi.create.foundation.data.recipe.CreateRecipeProvider;
import com.simibubi.create.foundation.data.recipe.CreateSequencedAssemblyRecipeGen;
import com.simibubi.create.foundation.data.recipe.CreateStandardRecipeGen;

import com.simibubi.create.foundation.ponder.CreatePonderPlugin;
import com.simibubi.create.foundation.utility.FilesHelper;
import com.tterrag.registrate.providers.ProviderType;

import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.core.RegistrySetBuilder;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

import io.github.fabricators_of_create.porting_lib.data.ExistingFileHelper;

public class CreateDatagen implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
		ExistingFileHelper helper = ExistingFileHelper.withResourcesFromArg();
		FabricDataGenerator.Pack pack = generator.createPack();
		Create.registrate().setupDatagen(pack, helper);
		gatherData(pack, helper);
	}

	public static void gatherData(FabricDataGenerator.Pack pack, ExistingFileHelper existingFileHelper) {
		addExtraRegistrateData();

		// fabric: tag lang
		TagLangGen.datagen();
		// fabric: archex compat
		ArchExCompat.init(pack);

		// fabric: pretty much redone, make sure all providers make it through merges

		generator.addProvider(event.includeServer(), new CreateRecipeSerializerTagsProvider(output, lookupProvider, existingFileHelper));
		generator.addProvider(event.includeServer(), new CreateContraptionTypeTagsProvider(output, lookupProvider, existingFileHelper));
		generator.addProvider(event.includeServer(), new CreateMountedItemStorageTypeTagsProvider(output, lookupProvider, existingFileHelper));
		generator.addProvider(event.includeServer(), new DamageTypeTagGen(output, lookupProvider, existingFileHelper));
		generator.addProvider(event.includeServer(), new AllAdvancements(output, lookupProvider));
		generator.addProvider(event.includeServer(), new CreateStandardRecipeGen(output, lookupProvider));
		generator.addProvider(event.includeServer(), new CreateMechanicalCraftingRecipeGen(output, lookupProvider));
		generator.addProvider(event.includeServer(), new CreateSequencedAssemblyRecipeGen(output, lookupProvider));
		generator.addProvider(event.includeServer(), new CreateDatamapProvider(output, lookupProvider));
		generator.addProvider(event.includeServer(), new VanillaHatOffsetGenerator(output, lookupProvider));
		generator.addProvider(event.includeServer(), new CuriosDataGenerator(output, lookupProvider, existingFileHelper));
		generator.addProvider(event.includeServer(), new CreateEnchantmentTagsProvider(output, lookupProvider, existingFileHelper));
		generator.addProvider(event.includeClient(), new CreateWikiBlockInfoProvider(output));

		if (event.includeServer()) {
			CreateRecipeProvider.registerAllProcessing(generator, output, lookupProvider);
		}
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
