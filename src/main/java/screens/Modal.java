package screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.AlertScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class Modal {
    private static Screen current;

    public static void alert(Component title, Component text) {
        Minecraft mc = Minecraft.getInstance();
        Screen previous = mc.screen;
        System.out.println("previous = " + previous);
        System.out.println("current before = " + mc.screen);
        mc.setScreen(new AlertScreen(() -> mc.setScreen(previous), title, text));
    }
}