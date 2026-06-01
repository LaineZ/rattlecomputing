package ru.bpm140.rattlecomputing.network.packets;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;


public record SelfDestructPacket(BlockPos pos) implements CustomPacketPayload {

    public static final Type<SelfDestructPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("rattlecomputing", "self_destruct"));

    public static final StreamCodec<FriendlyByteBuf, SelfDestructPacket> CODEC =
            StreamCodec.of(
                    (buf, msg) -> buf.writeBlockPos(msg.pos),
                    buf -> new SelfDestructPacket(buf.readBlockPos())
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}