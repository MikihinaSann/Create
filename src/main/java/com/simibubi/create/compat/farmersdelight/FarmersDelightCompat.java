package com.simibubi.create.compat.farmersdelight;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class FarmersDelightCompat {
	private static final ResourceLocation RICH_SOIL = ResourceLocation.fromNamespaceAndPath("farmersdelight", "rich_soil");

	public static boolean shouldHarvestMushroom(Level world, BlockPos pos, BlockState state) {
		Block below = world.getBlockState(pos.below()).getBlock();
		return !BuiltInRegistries.BLOCK.getKey(below).equals(RICH_SOIL);
	}
}
