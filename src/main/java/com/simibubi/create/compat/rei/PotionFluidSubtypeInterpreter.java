package com.simibubi.create.compat.rei;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.fluids.potion.PotionFluid.BottleType;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.PotionContents;

import com.simibubi.create.infrastructure.fabric.transfer.fluid.FluidStack;

/* From JEI's Potion item subtype interpreter */
public class PotionFluidSubtypeInterpreter /*implements IIngredientSubtypeInterpreter<FluidStack>*/ {

//	@Override
	public String apply(FluidStack ingredient) {
		PotionContents contents = ingredient.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
		if (contents.equals(PotionContents.EMPTY))
			return "";

		String potionTypeString = contents.potion()
			.map(potion -> potion.value().getEffects().isEmpty() ? ""
				: potion.unwrapKey().map(k -> k.location().toString()).orElse(""))
			.orElse("");
		String bottleType = ingredient.getOrDefault(AllDataComponents.POTION_FLUID_BOTTLE_TYPE, BottleType.REGULAR)
			.toString();

		StringBuilder stringBuilder = new StringBuilder(potionTypeString);
		stringBuilder.append(";").append(bottleType);
		for (MobEffectInstance effect : contents.getAllEffects())
			stringBuilder.append(";").append(effect);
		return stringBuilder.toString();
	}
}
