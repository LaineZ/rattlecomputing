package ru.bpm140.rattlecomputing;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import ru.bpm140.rattlecomputing.screens.McuBlockScreen;

@EventBusSubscriber(modid = Rattlecomputing.MODID, value = Dist.CLIENT)
public class ClientScreens {

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(
                Rattlecomputing.MCU_BLOCK_MENU.get(),
                McuBlockScreen::new
        );
    }
}