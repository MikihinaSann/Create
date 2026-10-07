package com.simibubi.create.foundation.mixin.fabric;

import java.util.Map;
import java.util.function.BiConsumer;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.tterrag.registrate.providers.loot.RegistrateLootTables;

import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

// fabric: RegistrateBlockLootTables extends BlockLootSubProvider whose generate()
// requires a table for every block in the registry. Registrate only populates its
// own entries, so emit the collected map directly like FabricBlockLootTableProvider.
@Mixin(BlockLootSubProvider.class)
public abstract class BlockLootSubProviderMixin {
	@Shadow
	@Final
	protected Map<ResourceKey<LootTable>, LootTable.Builder> map;

	@Shadow
	protected abstract void generate();

	@Inject(method = "generate(Ljava/util/function/BiConsumer;)V", at = @At("HEAD"), cancellable = true)
	private void create$generateOwnEntries(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer,
			CallbackInfo ci) {
		if ((Object) this instanceof RegistrateLootTables) {
			generate();
			map.forEach(consumer);
			ci.cancel();
		}
	}
}
