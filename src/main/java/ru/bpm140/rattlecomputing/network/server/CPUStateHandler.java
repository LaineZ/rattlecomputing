package ru.bpm140.rattlecomputing.network.server;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import ru.bpm140.rattlecomputing.network.CPUClientStore;
import ru.bpm140.rattlecomputing.network.packets.CPUStatePacket;

public class CPUStateHandler {
    public static void register(PayloadRegistrar registrar) {
        registrar.playToClient(
                CPUStatePacket.TYPE,
                CPUStatePacket.CODEC,
                CPUStateHandler::handle
        );
    }

    private static void handle(CPUStatePacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            CPUClientStore.apply(msg.pos(), msg.state());
        });
    }
}