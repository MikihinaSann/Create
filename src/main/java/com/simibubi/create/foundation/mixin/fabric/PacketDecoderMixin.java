package com.simibubi.create.foundation.mixin.fabric;

import java.io.IOException;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.Create;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.PacketDecoder;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;

// TEMPORARY DIAGNOSTIC: identify the channel of a custom_payload that under-reads
// during decode (production "found N bytes extra" disconnects). Remove once found.
@Mixin(PacketDecoder.class)
public abstract class PacketDecoderMixin {
	@Inject(method = "decode", at = @At(value = "NEW", target = "java/io/IOException"))
	private void create$logOversizedPayload(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out,
			CallbackInfo ci, @Local Packet<?> packet) {
		StringBuilder hex = new StringBuilder();
		int peek = Math.min(buf.readableBytes(), 96);
		for (int i = 0; i < peek; i++) {
			hex.append(String.format("%02x", buf.getByte(buf.readerIndex() + i) & 0xff));
			if (i % 2 == 1)
				hex.append(' ');
		}
		if (packet instanceof ClientboundCustomPayloadPacket p) {
			Create.LOGGER.error("[PACKETDEBUG] oversized custom_payload channel={} leftover={}B head={}",
					p.payload().type().id(), buf.readableBytes(), hex);
		} else {
			Create.LOGGER.error("[PACKETDEBUG] oversized packet type={} leftover={}B head={}",
					packet.type(), buf.readableBytes(), hex);
		}
	}
}
