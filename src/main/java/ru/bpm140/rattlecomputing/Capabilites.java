package ru.bpm140.rattlecomputing;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import static net.neoforged.neoforge.internal.versions.neoforge.NeoForgeVersion.MOD_ID;
import static ru.bpm140.rattlecomputing.Rattlecomputing.MCU_BLOCK_ENTITY;

@EventBusSubscriber(modid = MOD_ID)
public class Capabilites {
    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                MCU_BLOCK_ENTITY.get(),
                (blockEntity, side) -> blockEntity.inventory
        );
    }
}
