package ru.bpm140.rattlecomputing.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import ru.bpm140.rattlecomputing.network.server.CPUStateHandler;
import ru.bpm140.rattlecomputing.network.server.FirmwareUploadHandler;
import ru.bpm140.rattlecomputing.network.server.SelfDestructHandler;
import ru.bpm140.rattlecomputing.network.server.SetCPUStatusHandler;

public class Networking {
    public static void register(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1");

        SelfDestructHandler.register(registrar);
        FirmwareUploadHandler.register(registrar);
        SetCPUStatusHandler.register(registrar);
        CPUStateHandler.register(registrar);
    }
}