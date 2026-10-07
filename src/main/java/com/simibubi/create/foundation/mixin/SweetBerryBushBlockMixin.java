package com.simibubi.create.foundation.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import com.simibubi.create.api.behaviour.SpecialPlantable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SweetBerryBushBlock;

@Mixin(SweetBerryBushBlock.class)
public abstract class SweetBerryBushBlockMixin extends Block implements SpecialPlantable {

	protected SweetBerryBushBlockMixin(Properties properties) {
		super(properties);
	}

	@Override
	public boolean canPlacePlantAtPosition(ItemStack stack, LevelAccessor level, BlockPos pos,
		@Nullable Direction direction) {
		return defaultBlockState().canSurvive(level, pos);
	}

	@Override
	public void spawnPlantAtPosition(ItemStack stack, LevelAccessor level, BlockPos pos,
		@Nullable Direction direction) {
		level.setBlock(pos, defaultBlockState(), Block.UPDATE_ALL);
	}
}
