package com.simibubi.create.api.registry;

import com.mojang.serialization.Codec;
import com.simibubi.create.impl.registry.CreateDataMapsImpl;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

/**
 * fabric port of NeoForge's {@code DataMapType}: a descriptor for datapack-driven
 * per-registry-object data, loaded from {@code data/<namespace>/data_maps/<registry>/<name>.json}.
 *
 * @param <R> the registry type
 * @param <T> the attached value type
 */
public record DataMapType<R, T>(ResourceLocation id, ResourceKey<? extends Registry<R>> registryKey,
								Codec<T> codec) {

	public static <R, T> Builder<R, T> builder(ResourceLocation id,
											 ResourceKey<? extends Registry<R>> registryKey, Codec<T> codec) {
		return new Builder<>(id, registryKey, codec);
	}

	public static final class Builder<R, T> {
		private final ResourceLocation id;
		private final ResourceKey<? extends Registry<R>> registryKey;
		private final Codec<T> codec;

		private Builder(ResourceLocation id, ResourceKey<? extends Registry<R>> registryKey, Codec<T> codec) {
			this.id = id;
			this.registryKey = registryKey;
			this.codec = codec;
		}

		public DataMapType<R, T> build() {
			DataMapType<R, T> type = new DataMapType<>(id, registryKey, codec);
			CreateDataMapsImpl.registerType(type);
			return type;
		}
	}
}
