package com.simibubi.create.foundation.fluid;

import java.util.List;
import java.util.function.Predicate;

import com.mojang.serialization.Codec;
import com.simibubi.create.infrastructure.fabric.transfer.fluid.FluidStack;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

// fabric: port of neoforge's SizedFluidIngredient. Our FluidIngredient already carries an
// amount, so this wrapper keeps it authoritative by writing the sized amount back into it.
public record SizedFluidIngredient(FluidIngredient ingredient, long amount) implements Predicate<FluidStack> {

	public SizedFluidIngredient {
		ingredient.amountRequired = amount;
	}

	public static final Codec<SizedFluidIngredient> CODEC =
		FluidIngredient.CODEC.xmap(ingredient -> new SizedFluidIngredient(ingredient, ingredient.getRequiredAmount()),
			SizedFluidIngredient::ingredient);

	public static final StreamCodec<RegistryFriendlyByteBuf, SizedFluidIngredient> STREAM_CODEC =
		FluidIngredient.STREAM_CODEC.map(i -> new SizedFluidIngredient(i, i.getRequiredAmount()), SizedFluidIngredient::ingredient);

	public static SizedFluidIngredient of(FluidStack stack) {
		return new SizedFluidIngredient(FluidIngredient.fromFluidStack(stack), stack.getAmount());
	}

	public static SizedFluidIngredient of(Fluid fluid, long amount) {
		return new SizedFluidIngredient(FluidIngredient.fromFluid(fluid, amount), amount);
	}

	public static SizedFluidIngredient of(TagKey<Fluid> tag, long amount) {
		return new SizedFluidIngredient(FluidIngredient.fromTag(tag, amount), amount);
	}

	@Override
	public boolean test(FluidStack stack) {
		return ingredient.test(stack);
	}

	public List<FluidStack> getFluids() {
		return ingredient.getMatchingFluidStacks();
	}

	public List<FluidStack> getMatchingFluidStacks() {
		return ingredient.getMatchingFluidStacks();
	}

	public boolean hasNoFluids() {
		return getFluids().isEmpty();
	}

	public boolean isEmpty() {
		return ingredient == FluidIngredient.EMPTY || amount <= 0;
	}
}
