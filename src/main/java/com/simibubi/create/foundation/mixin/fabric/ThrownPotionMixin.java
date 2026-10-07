package com.simibubi.create.foundation.mixin.fabric;

import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.core.component.DataComponents;

// milk-lib's PotionEntityMixin shadows and invokes ThrownPotion methods by their
// intermediary names (method_7497..method_7500). In 1.21.1 those methods were renamed
// and applySplash's signature changed from List to Iterable, so milk's shadows cannot
// be satisfied. This mixin restores the stale intermediary methods as delegating shims.
// Priority < 1000 so it applies before milk's mixin (default priority).
@Mixin(value = ThrownPotion.class, priority = 500)
public abstract class ThrownPotionMixin {
	@Invoker("applySplash")
	abstract void create$invokeApplySplash(Iterable<MobEffectInstance> effects, @Nullable Entity entity);

	@Invoker("applyWater")
	abstract void create$invokeApplyWater();

	@Invoker("dowseFire")
	abstract void create$invokeDowseFire(BlockPos pos);

	@Invoker("makeAreaOfEffectCloud")
	abstract void create$invokeMakeAreaOfEffectCloud(PotionContents contents);

	@Unique
	protected void method_7497(@Nullable ItemStack stack, @Nullable Potion potion) {
		this.create$invokeMakeAreaOfEffectCloud(((ThrownPotion) (Object) this).getItem()
			.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY));
	}

	@Unique
	protected void method_7498(@Nullable List<MobEffectInstance> effects, @Nullable Entity entity) {
		this.create$invokeApplySplash(effects == null ? List.of() : effects, entity);
	}

	@Unique
	protected void method_7499(BlockPos pos) {
		this.create$invokeDowseFire(pos);
	}

	@Unique
	protected void method_7500() {
		this.create$invokeApplyWater();
	}
}
