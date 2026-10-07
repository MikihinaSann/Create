package com.simibubi.create.infrastructure.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.simibubi.create.content.equipment.goggles.GoggleConfigScreen;
import com.simibubi.create.infrastructure.config.AllConfigs;

import net.createmod.catnip.gui.ScreenOpener;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class OverlayConfigCommand {
	public static ArgumentBuilder<CommandSourceStack, ?> register() {
		return Commands.literal("overlay")
			.requires(cs -> cs.hasPermission(0))
			.then(Commands.literal("reset")
				.executes(ctx -> {
					AllConfigs.client().overlayOffsetX.set(0);
					AllConfigs.client().overlayOffsetY.set(0);

					ctx.getSource().sendSuccess(() -> Component.literal("Create Goggle Overlay has been reset to default position"), true);
					return Command.SINGLE_SUCCESS;
				})
			)
			.executes(ctx -> {
				Client.openScreen();
				return Command.SINGLE_SUCCESS;
			});

	}

	// fabric: lazily-loaded so the enclosing class verifies on a dedicated server;
	// only ever invoked from the client command
	@Environment(EnvType.CLIENT)
	static class Client {
		static void openScreen() {
			ScreenOpener.open(new GoggleConfigScreen());
		}
	}
}
