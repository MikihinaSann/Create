package com.simibubi.create.foundation.utility;

import java.util.Optional;

import io.github.fabricators_of_create.porting_lib.common.util.EnvExecutor;
import net.fabricmc.api.EnvType;
import io.github.fabricators_of_create.porting_lib.core.util.ServerLifecycleHooks;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * Access to the dynamic enchantment registry from contexts that don't carry a
 * Level/RegistryAccess (inside Enchantment or ItemStack mixin hooks).
 */
public class EnchantmentLookup {

	private static RegistryAccess registryAccess() {
		return EnvExecutor.unsafeRunForDist(
			() -> () -> Minecraft.getInstance().level == null ? null
				: Minecraft.getInstance().level.registryAccess(),
			() -> () -> {
				MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
				return server == null ? null : server.registryAccess();
			});
	}

	public static Optional<Registry<Enchantment>> enchantments() {
		RegistryAccess access = registryAccess();
		if (access == null)
			return Optional.empty();
		return access.registry(Registries.ENCHANTMENT);
	}

	public static Optional<HolderLookup.RegistryLookup<Enchantment>> lookup() {
		RegistryAccess access = registryAccess();
		if (access == null)
			return Optional.empty();
		return access.lookup(Registries.ENCHANTMENT);
	}

	public static Optional<Holder.Reference<Enchantment>> holderOf(Enchantment enchantment) {
		return enchantments().flatMap(registry -> registry.getResourceKey(enchantment)
			.map(registry::getHolderOrThrow));
	}
}
