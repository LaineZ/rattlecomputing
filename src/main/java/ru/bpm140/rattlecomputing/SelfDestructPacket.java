package ru.bpm140.rattlecomputing;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;


public record SelfDestructPacket(BlockPos pos) implements CustomPacketPayload {

    public static final Type<SelfDestructPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("rattlecomputing", "self_destruct"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, SelfDestructPacket> CODEC =
            StreamCodec.of(
                    (buf, msg) -> buf.writeBlockPos(msg.pos),
                    buf -> new SelfDestructPacket(buf.readBlockPos())
            );
}