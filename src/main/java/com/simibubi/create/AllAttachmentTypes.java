package com.simibubi.create;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.simibubi.create.content.contraptions.minecart.capability.MinecartController;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

public class AllAttachmentTypes {
	public static final AttachmentType<MinecartController> MINECART_CONTROLLER =
		AttachmentRegistry.<MinecartController>builder()
			.initializer(() -> MinecartController.EMPTY)
			.persistent(MinecartController.SERIALIZER)
			.buildAndRegister(Create.asResource("minecart_controller"));

	@Internal
	public static void register() {
	}
}
