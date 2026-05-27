package ru.bpm140.rattlecomputing.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record FirmwareUploadPacket(String path, String firmwareName, byte[] data, ItemStack stack) implements CustomPacketPayload {

    public static final Type<FirmwareUploadPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("rattlecomputing", "firmware_upload"));

    public static final StreamCodec<FriendlyByteBuf, FirmwareUploadPacket> CODEC =
            StreamCodec.of(
                    (buf, msg) -> {
                        buf.writeUtf(msg.path());
                        buf.writeUtf(msg.firmwareName());
                        buf.writeInt(msg.data().length);
                        buf.writeBytes(msg.data());
                        ItemStack.STREAM_CODEC.encode((RegistryFriendlyByteBuf) buf, msg.stack());
                    },
                    buf -> {
                        String path = buf.readUtf();
                        String firmwareName = buf.readUtf();
                        int len = buf.readInt();

                        byte[] data = new byte[len];
                        buf.readBytes(data);
                        ItemStack stack = ItemStack.STREAM_CODEC.decode((RegistryFriendlyByteBuf) buf);

                        return new FirmwareUploadPacket(path, firmwareName, data, stack);
                    }
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return FirmwareUploadPacket.TYPE;
    }
}
