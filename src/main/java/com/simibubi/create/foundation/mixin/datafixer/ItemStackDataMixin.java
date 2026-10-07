package com.simibubi.create.foundation.mixin.datafixer;

import org.spongepowered.asm.mixin.Mixin;

import com.simibubi.create.foundation.utility.ItemStackDataAccessor;

// fabric: applies the duck interface so handlers can @Coerce the record to it
@Mixin(targets = "net.minecraft.util.datafix.fixes.ItemStackComponentizationFix$ItemStackData")
public abstract class ItemStackDataMixin implements ItemStackDataAccessor {
}
