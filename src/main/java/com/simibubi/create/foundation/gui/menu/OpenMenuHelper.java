package com.simibubi.create.foundation.gui.menu;

import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

/**
 * Helpers for opening Create's extended menus server-side.
 *
 * <p>All of Create's menus serialize their extra opening data into a raw
 * {@link RegistryFriendlyByteBuf}, matching what NeoForge's {@code openMenu}
 * overloads did. Fabric's {@link net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType}
 * requires a packet codec for that data, so {@link #RAW_BUFFER_CODEC} passes the
 * buffer through unmodified.</p>
 */
public class OpenMenuHelper {

	/**
	 * Codec that exposes the raw {@link RegistryFriendlyByteBuf} as the data object.
	 * Encoding appends the data buffer's readable bytes; decoding must consume them
	 * (the packet decoder errors on unread payload bytes), so the data is copied into
	 * a fresh buffer for the menu factory to read its fields from.
	 */
	public static final StreamCodec<RegistryFriendlyByteBuf, RegistryFriendlyByteBuf> RAW_BUFFER_CODEC =
		StreamCodec.of(
			(buf, data) -> buf.writeBytes(data.copy()),
			buf -> new RegistryFriendlyByteBuf(buf.readBytes(buf.readableBytes()), buf.registryAccess())
		);

	public static MenuProvider create(MenuProvider provider, Consumer<RegistryFriendlyByteBuf> extraDataWriter) {
		return new ExtendedScreenHandlerFactory<RegistryFriendlyByteBuf>() {
			@Override
			public RegistryFriendlyByteBuf getScreenOpeningData(ServerPlayer player) {
				RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(
					Unpooled.buffer(), player.registryAccess());
				extraDataWriter.accept(buf);
				return buf;
			}

			@Override
			public Component getDisplayName() {
				return provider.getDisplayName();
			}

			@Nullable
			@Override
			public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
				return provider.createMenu(syncId, playerInventory, player);
			}
		};
	}

	public static MenuProvider create(MenuProvider provider, BlockPos pos) {
		return create(provider, buf -> buf.writeBlockPos(pos));
	}
}
