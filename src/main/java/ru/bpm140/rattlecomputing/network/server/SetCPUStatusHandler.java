package ru.bpm140.rattlecomputing.network.server;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import ru.bpm140.rattlecomputing.blockentities.McuBlockEntity;
import ru.bpm140.rattlecomputing.network.packets.SetCPUStatusPacket;

public class SetCPUStatusHandler {
    public static void register(PayloadRegistrar registrar) {
        registrar.playToServer(
                SetCPUStatusPacket.TYPE,
                SetCPUStatusPacket.CODEC,
                SetCPUStatusHandler::handle
        );
    }

    private static void handle(SetCPUStatusPacket msg, IPayloadContext ctx) {
        ServerPlayer player = (ServerPlayer) ctx.player();

        Level level = player.level();
        BlockEntity be = level.getBlockEntity(msg.pos());

        if (be instanceof McuBlockEntity mcu) {
            if (msg.action() == SetCPUStatusPacket.Action.POWER) {
                // TODO: Power cycle
                mcu.startSoc(player);
            }

            if (msg.action() == SetCPUStatusPacket.Action.RESET) {
                // TODO: Reset
            }
        }
    }
}
