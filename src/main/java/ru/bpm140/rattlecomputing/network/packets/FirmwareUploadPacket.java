package ru.bpm140.rattlecomputing.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record FirmwareUploadPacket(String originalPath, byte[] data) implements CustomPacketPayload {

    public static final Type<FirmwareUploadPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("rattlecomputing", "firmware_upload"));

    public static final StreamCodec<FriendlyByteBuf, FirmwareUploadPacket> CODEC =
            StreamCodec.of(
                    (buf, msg) -> {
                        buf.writeUtf(msg.originalPath());
                        buf.writeInt(msg.data().length);
                        buf.writeBytes(msg.data());
                    },
                    buf -> {
                        String path = buf.readUtf();
                        int len = buf.readInt();

                        byte[] data = new byte[len];
                        buf.readBytes(data);

                        return new FirmwareUploadPacket(path, data);
                    }
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return FirmwareUploadPacket.TYPE;
    }
}
