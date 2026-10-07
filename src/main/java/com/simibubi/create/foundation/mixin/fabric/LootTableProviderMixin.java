package com.simibubi.create.foundation.mixin.fabric;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tterrag.registrate.providers.loot.RegistrateLootTableProvider;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

// fabric: RegistrateLootTableProvider constructs itself with vanilla's subprovider
// list (which crashes on modded blocks) and never uses its own getTables(). Swap the
// iterated list for the registrate-generated one during datagen.
@Mixin(LootTableProvider.class)
public abstract class LootTableProviderMixin {
	@Unique
	private PackOutput create$output;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void create$captureOutput(PackOutput output, Set<ResourceKey<LootTable>> requiredTables,
			List<LootTableProvider.SubProviderEntry> subProviders,
			CompletableFuture<HolderLookup.Provider> registries, CallbackInfo ci) {
		this.create$output = output;
	}

	@ModifyExpressionValue(method = "run(Lnet/minecraft/data/CachedOutput;Lnet/minecraft/core/HolderLookup$Provider;)Ljava/util/concurrent/CompletableFuture;", at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/data/loot/LootTableProvider;subProviders:Ljava/util/List;",
			opcode = Opcodes.GETFIELD))
	private List<LootTableProvider.SubProviderEntry> create$registrateSubProviders(
			List<LootTableProvider.SubProviderEntry> original) {
		if ((Object) this instanceof RegistrateLootTableProvider provider
				&& this.create$output instanceof FabricDataOutput fabricOutput)
			return provider.getTables(fabricOutput);
		return original;
	}
}
