package com.simibubi.create.foundation.mixin.fabric;

import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.math.Transformation;

import org.apache.commons.lang3.tuple.Pair;

import io.github.fabricators_of_create.porting_lib.models.obj.ObjModel;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;

// fabric: porting-lib beta.47's ObjModel.makeQuad configures the QuadEmitter but never
// calls emit(), so MeshBuilder.build() returns an empty mesh and the subsequent
// quadRef.get().toBakedQuad() NPEs. Upstream fixed this post-beta.47 with an explicit
// quadBaker.emit() before build() - mirror that here.
@Mixin(ObjModel.class)
public abstract class ObjModelMixin {
	@Inject(method = "makeQuad", at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/api/renderer/v1/mesh/MeshBuilder;build()Lnet/fabricmc/fabric/api/renderer/v1/mesh/Mesh;"))
	private void create$emitPendingQuad(int[][] indices, int tintIndex, Vector4f colorTint,
			Vector4f ambientColor, TextureAtlasSprite texture, Transformation transform,
			CallbackInfoReturnable<Pair<BakedQuad, Direction>> cir, @Local QuadEmitter quadBaker) {
		quadBaker.emit();
	}
}
