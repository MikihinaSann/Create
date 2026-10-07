package com.simibubi.create.api.behaviour;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;

// fabric: replacement for NeoForge's SpecialPlantable, implemented onto SweetBerryBushBlock via mixin
public interface SpecialPlantable {
	void spawnPlantAtPosition(ItemStack stack, LevelAccessor level, BlockPos pos, @Nullable Direction direction);

	boolean canPlacePlantAtPosition(ItemStack stack, LevelAccessor level, BlockPos pos, @Nullable Direction direction);
}
