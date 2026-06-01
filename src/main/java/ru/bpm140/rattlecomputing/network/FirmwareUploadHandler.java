package ru.bpm140.rattlecomputing.network;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import ru.bpm140.rattlecomputing.items.CartridgeItem;
import ru.bpm140.rattlecomputing.network.packets.FirmwareUploadPacket;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class FirmwareUploadHandler {
    public static void register(PayloadRegistrar registrar) {
        registrar.playToServer(
                FirmwareUploadPacket.TYPE,
                FirmwareUploadPacket.CODEC,
                FirmwareUploadHandler::handle
        );
    }


    private static void handle(FirmwareUploadPacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) ctx.player();
            ServerLevel level = player.serverLevel();

            ItemStack stack = player.getMainHandItem();

            if (!(stack.getItem() instanceof CartridgeItem)) return;

            Path worldDir = level.getServer()
                    .getWorldPath(LevelResource.ROOT)
                    .resolve("rattlecomputing/firmware");

            try {
                Files.createDirectories(worldDir);

                String id = UUID.randomUUID().toString();
                Path file = worldDir.resolve(id);

                Path original = Path.of(msg.originalPath());

                Files.write(file, msg.data());

                CartridgeItem.setFirmware(
                        stack,
                        msg.originalPath(),
                        file.toString(),
                        original.getFileName().toString()
                );

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}