package ru.bpm140.rattlecomputing.network.packets;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import ru.bpm140.rottenmangal.CPUStatus;

public record CPUStatePacket(BlockPos pos, CPUStatus.CPUState state) implements CustomPacketPayload {

    public static final Type<CPUStatePacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("rattlecomputing", "cpu_state"));

    public static final StreamCodec<FriendlyByteBuf, CPUStatePacket> CODEC =
            StreamCodec.of(
                    (buf, msg) -> {
                        buf.writeBlockPos(msg.pos());
                        buf.writeEnum(msg.state());
                    },
                    buf -> new CPUStatePacket(
                            buf.readBlockPos(),
                            buf.readEnum(CPUStatus.CPUState.class)
                    )
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}