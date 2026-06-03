package ru.bpm140.rattlecomputing.network.packets;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SetCPUStatusPacket(BlockPos pos, Action action) implements CustomPacketPayload {
    public enum Action {
        POWER,
        RESET
    }

    public static final Type<SetCPUStatusPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("rattlecomputing", "cpu_power_control"));

    public static final StreamCodec<FriendlyByteBuf, SetCPUStatusPacket> CODEC =
            StreamCodec.of(
                    (buf, msg) -> {
                        buf.writeBlockPos(msg.pos());
                        buf.writeEnum(msg.action());
                    },
                    buf -> new SetCPUStatusPacket(
                            buf.readBlockPos(),
                            SetCPUStatusPacket.Action.values()[buf.readInt()]
                    )
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}