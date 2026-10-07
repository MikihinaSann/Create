package com.simibubi.create.foundation.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.simibubi.create.content.trains.CameraDistanceModifier;
import com.simibubi.create.foundation.events.ClientEvents;

import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;

@Mixin(Camera.class)
public abstract class CameraMixin {
	@ModifyArg(
			method = "setup(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/world/entity/Entity;ZZF)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;getMaxZoom(F)F"),
			index = 0
	)
	private float create$modifyCameraOffset(float originalValue) {
		return originalValue * CameraDistanceModifier.getMultiplier();
	}

	// fabric port: upstream fires NeoForge's CameraSetupEvent here (old porting-lib
	// CameraSetupCallback); apply the yaw/pitch animation directly
	@Inject(method = "setup(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/world/entity/Entity;ZZF)V", at = @At("RETURN"))
	private void create$applyCameraAngleAnimation(BlockGetter level, Entity entity, boolean detached,
												  boolean mirrored, float partialTick, CallbackInfo ci) {
		ClientEvents.onCameraSetup((Camera) (Object) this);
	}
}
