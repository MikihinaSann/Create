package com.simibubi.create.foundation.mixin.fabric;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.fabricators_of_create.porting_lib.models.ExtraFaceData;
import io.github.fabricators_of_create.porting_lib.models.geometry.extensions.BlockElementExt;
import net.minecraft.client.renderer.block.model.BlockElement;

// fabric: porting-lib's BlockElementMixin never initializes port_lib$faceData, so
// ModelBuilder#toJson NPEs on elements built programmatically during datagen.
// Default it to ExtraFaceData.DEFAULT, matching NeoForge's field initializer.
@Mixin(BlockElement.class)
public abstract class BlockElementFaceDataMixin {
	@Inject(method = "<init>", at = @At("RETURN"))
	private void create$initFaceData(CallbackInfo ci) {
		BlockElementExt self = (BlockElementExt) this;
		if (self.port_lib$getFaceData() == null)
			self.port_lib$setFaceData(ExtraFaceData.DEFAULT);
	}
}
