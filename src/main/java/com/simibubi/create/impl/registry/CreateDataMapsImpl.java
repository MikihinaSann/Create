package com.simibubi.create.impl.registry;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.simibubi.create.Create;
import com.simibubi.create.api.registry.DataMapType;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagKey;

/**
 * fabric port of NeoForge's data map system. Loads
 * {@code data/<namespace>/data_maps/<registry path>/<data map namespace>/<name>.json}
 * files in the same format NeoForge uses: an object with an optional {@code "replace"}
 * flag and a {@code "values"} object mapping entry ids or {@code "#tag"} keys to values.
 */
public class CreateDataMapsImpl {
	static final Map<ResourceLocation, DataMapType<?, ?>> TYPES = new HashMap<>();

	// values attached directly to registry entries, keyed by entry id
	private static final Map<DataMapType<?, ?>, Map<ResourceLocation, Object>> LOADED_VALUES = new IdentityHashMap<>();
	// values attached to tags, applied to all tag members
	private static final Map<DataMapType<?, ?>, Map<ResourceLocation, Object>> LOADED_TAG_VALUES = new IdentityHashMap<>();

	public static <R, T> void registerType(DataMapType<R, T> type) {
		TYPES.put(type.id(), type);
	}

	public static void register() {
		ResourceManagerHelper.get(PackType.SERVER_DATA)
			.registerReloadListener(new SimpleSynchronousResourceReloadListener() {
				@Override
				public ResourceLocation getFabricId() {
					return Create.asResource("data_maps");
				}

				@Override
				public void onResourceManagerReload(ResourceManager manager) {
					CreateDataMapsImpl.reload(manager);
				}
			});
	}

	private static void reload(ResourceManager manager) {
		LOADED_VALUES.clear();
		LOADED_TAG_VALUES.clear();

		for (DataMapType<?, ?> type : TYPES.values())
			loadType(manager, type);
	}

	private static <R, T> void loadType(ResourceManager manager, DataMapType<R, T> type) {
		// files may be provided under any namespace:
		// data/<any>/data_maps/<registry>/<datamap namespace>/<name>.json
		String directory = "data_maps/" + type.registryKey().location().getPath() + "/" + type.id().getNamespace();
		String fileName = type.id().getPath() + ".json";

		Map<ResourceLocation, Object> values = LOADED_VALUES.computeIfAbsent(type, t -> new HashMap<>());
		Map<ResourceLocation, Object> tagValues = LOADED_TAG_VALUES.computeIfAbsent(type, t -> new HashMap<>());

		manager.listResourceStacks(directory, location -> location.getPath().endsWith(".json")).forEach((location, resources) -> {
			if (!location.getPath().equals(directory + "/" + fileName))
				return;
			loadFile(type, location, resources, values, tagValues);
		});
	}

	private static <R, T> void loadFile(DataMapType<R, T> type, ResourceLocation file, List<Resource> resources,
										Map<ResourceLocation, Object> values, Map<ResourceLocation, Object> tagValues) {
		for (Resource resource : resources) {
			try (var reader = resource.openAsReader()) {
				JsonElement root = JsonParser.parseReader(reader);
				if (!root.isJsonObject())
					continue;
				JsonObject json = root.getAsJsonObject();
				if (json.has("replace") && json.get("replace").getAsBoolean()) {
					values.clear();
					tagValues.clear();
				}
				JsonObject valueMap = json.getAsJsonObject("values");
				if (valueMap == null)
					continue;
				for (Map.Entry<String, JsonElement> entry : valueMap.entrySet()) {
					DataResult<T> parsed = type.codec().parse(JsonOps.INSTANCE, entry.getValue());
					if (parsed.result().isEmpty()) {
						Create.LOGGER.warn("Failed to parse data map value '{}' in {}", entry.getKey(), file);
						continue;
					}
					String key = entry.getKey();
					if (key.startsWith("#")) {
						tagValues.put(ResourceLocation.parse(key.substring(1)), parsed.result().get());
					} else {
						values.put(ResourceLocation.parse(key), parsed.result().get());
					}
				}
			} catch (Exception e) {
				Create.LOGGER.error("Failed to load data map file {}", file, e);
			}
		}
	}


	/**
	 * Equivalent of NeoForge's {@code Holder#getData(DataMapType)}.
	 */
	@SuppressWarnings("unchecked")
	@Nullable
	public static <R, T> T getData(Holder<R> holder, DataMapType<R, T> type) {
		Map<ResourceLocation, Object> values = LOADED_VALUES.get(type);
		ResourceLocation key = holder.unwrapKey().map(ResourceKey::location).orElse(null);
		if (values != null && key != null) {
			Object value = values.get(key);
			if (value != null)
				return (T) value;
		}

		Map<ResourceLocation, Object> tagValues = LOADED_TAG_VALUES.get(type);
		if (tagValues != null) {
			for (Map.Entry<ResourceLocation, Object> entry : tagValues.entrySet()) {
				if (holder.is(TagKey.create(type.registryKey(), entry.getKey())))
					return (T) entry.getValue();
			}
		}
		return null;
	}

	private CreateDataMapsImpl() {
	}
}
