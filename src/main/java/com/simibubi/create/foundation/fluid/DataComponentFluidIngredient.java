package com.simibubi.create.foundation.fluid;

import com.simibubi.create.infrastructure.fabric.transfer.fluid.FluidStack;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.level.material.Fluid;

// fabric: stand-in for neoforge's DataComponentFluidIngredient. Our FluidIngredient already
// matches on fluid + component patch, so this is only a factory facade keeping upstream's
// call signatures working.
public final class DataComponentFluidIngredient {

	private DataComponentFluidIngredient() {}

	public static FluidIngredient of(boolean strict, FluidStack stack) {
		return FluidIngredient.fromFluidStack(stack);
	}

	public static FluidIngredient of(boolean strict, DataComponentPatch components, Fluid... fluids) {
		if (fluids.length == 0)
			return FluidIngredient.EMPTY;
		FluidIngredient.FluidStackIngredient ingredient = new FluidIngredient.FluidStackIngredient(fluids[0], components, 0);
		ingredient.fixFlowing();
		return ingredient;
	}

	public static FluidIngredient of(boolean strict, DataComponentPatch components, Fluid fluid) {
		return of(strict, components, new Fluid[] {fluid});
	}

	public static FluidIngredient ofSingle(DataComponentPatch components, Fluid fluid) {
		return of(true, components, fluid);
	}
}
