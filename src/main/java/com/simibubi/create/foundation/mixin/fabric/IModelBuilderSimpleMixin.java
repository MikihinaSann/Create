package com.simibubi.create.foundation.mixin.fabric;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import io.github.fabricators_of_create.porting_lib.models.IModelBuilder;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadView;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;

// fabric: porting-lib beta.47's IModelBuilder.Simple configures the shared QuadEmitter
// via fromVanilla()/copyFrom() but never calls emit(), so the quad is never committed
// to the MeshBuilder and build() produces an empty MeshBakedModel - invisible blocks
// and missing item icons for every custom-geometry model (OBJ, composite, etc).
// Note: emitting must happen on the same emitter instance - MeshBuilder.getEmitter()
// clears the maker, so emit() must be chained onto the call itself.
@Mixin(value = IModelBuilder.Simple.class, remap = false)
public abstract class IModelBuilderSimpleMixin {
	@WrapOperation(method = { "addCulledFace", "addUnculledFace" }, at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/api/renderer/v1/mesh/QuadEmitter;fromVanilla"))
	private QuadEmitter create$emitVanillaQuad(QuadEmitter emitter, BakedQuad quad,
			RenderMaterial material, Direction cullFace, Operation<QuadEmitter> operation) {
		QuadEmitter result = operation.call(emitter, quad, material, cullFace);
		result.emit();
		return result;
	}

	@WrapOperation(method = "addFace", at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/api/renderer/v1/mesh/QuadEmitter;copyFrom"))
	private QuadEmitter create$emitCopiedQuad(QuadEmitter emitter, QuadView quad,
			Operation<QuadEmitter> operation) {
		QuadEmitter result = operation.call(emitter, quad);
		result.emit();
		return result;
	}
}
