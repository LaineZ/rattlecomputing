package ru.bpm140.rattlecomputing.network.server;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import ru.bpm140.rattlecomputing.network.packets.SelfDestructPacket;

public class SelfDestructHandler {
    public static void register(PayloadRegistrar registrar) {
        registrar.playToServer(
                SelfDestructPacket.TYPE,
                SelfDestructPacket.CODEC,
                SelfDestructHandler::handle
        );
    }

    private static void handle(SelfDestructPacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) ctx.player();
            ServerLevel level = player.serverLevel();

            BlockPos pos = msg.pos();

            level.explode(
                    null,
                    pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5,
                    4.0f,
                    Level.ExplosionInteraction.BLOCK
            );
        });
    }
}