package ru.bpm140.rattlecomputing.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public class Networking {
    public static void register(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1");

        SelfDestructHandler.register(registrar);
        FirmwareUploadHandler.register(registrar);
    }
}