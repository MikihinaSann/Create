package com.simibubi.create.content.trains.entity;

import com.simibubi.create.AllPackets;
import com.simibubi.create.Create;

import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public record CarriageDataUpdatePacket(int entityId, CarriageSyncData data) implements ClientboundPacketPayload {
	public static final StreamCodec<FriendlyByteBuf, CarriageSyncData> DATA_CODEC = StreamCodec.of(
		(buffer, data) -> data.write(buffer),
		CarriageSyncData::new);

	public static final StreamCodec<FriendlyByteBuf, CarriageDataUpdatePacket> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.VAR_INT, CarriageDataUpdatePacket::entityId,
		DATA_CODEC, CarriageDataUpdatePacket::data,
		CarriageDataUpdatePacket::new);

	public CarriageDataUpdatePacket(CarriageContraptionEntity entity) {
		this(entity.getId(), entity.carriageData);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void handle(LocalPlayer player) {
		Entity entity = player.clientLevel.getEntity(entityId);
		if (entity instanceof CarriageContraptionEntity carriage) {
			carriage.onCarriageDataUpdate(data);
		} else {
			Create.LOGGER.error("Invalid CarriageDataUpdatePacket for non-carriage entity: {}", entityId);
		}
	}

	@Override
	public PacketTypeProvider getTypeProvider() {
		return AllPackets.CARRIAGE_DATA_UPDATE;
	}
}
