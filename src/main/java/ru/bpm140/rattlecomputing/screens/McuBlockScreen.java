package ru.bpm140.rattlecomputing.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import ru.bpm140.rattlecomputing.DisplayTexture;
import ru.bpm140.rattlecomputing.Shaders;
import ru.bpm140.rattlecomputing.menus.McuBlockMenu;
import ru.bpm140.rattlecomputing.screens.components.SMDImageButton;
import ru.bpm140.rattlecomputing.screens.immediate.LEDGlow;

@OnlyIn(Dist.CLIENT)
public class McuBlockScreen extends AbstractContainerScreen<McuBlockMenu> {
    private static final ResourceLocation CONTAINER_TEXTURE = ResourceLocation.fromNamespaceAndPath("rattlecomputing",
            "textures/gui/mcu_container.png");
    private DisplayTexture texture;

    private static final int CONTAINER_TEXTURE_WIDTH = 176;
    private static final int CONTAINER_TEXTURE_HEIGHT = 166;
    private static final int LED_GLOW_WIDTH = 12;
    private static final int LED_GLOW_HEIGHT = 7;

    private final LEDGlow LED = LEDGlow.GREEN();
    private final LEDGlow failLED = LEDGlow.RED();
    private final LEDGlow runLED = LEDGlow.BLUE();

    public McuBlockScreen(McuBlockMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Override
    protected void init() {
        super.init();
        if (texture == null) {
            texture = new DisplayTexture();
        }

        this.addRenderableWidget(new SMDImageButton(leftPos + 16, topPos + 32, btn -> Modal.alert(Component.literal("MCU Said:"), Component.literal("POWER"))));
        this.addRenderableWidget(new SMDImageButton(leftPos + 16, topPos + 40, btn -> Modal.alert(Component.literal("MCU Said:"), Component.literal("RESET"))));
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(g, mouseX, mouseY, partialTick);
        super.render(g, mouseX, mouseY, partialTick);
        this.renderTooltip(g, mouseX, mouseY);

    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        RenderSystem.setShaderTexture(0, CONTAINER_TEXTURE);
        guiGraphics.blit(CONTAINER_TEXTURE, leftPos, topPos, 0, 0, CONTAINER_TEXTURE_WIDTH, CONTAINER_TEXTURE_HEIGHT);

        var baseOffset = LED_GLOW_HEIGHT - 1;
        LED.render(guiGraphics,leftPos + 15, topPos + 47, LED_GLOW_WIDTH, LED_GLOW_HEIGHT);
        runLED.render(guiGraphics, leftPos + 15, topPos + 47 + baseOffset, LED_GLOW_WIDTH, LED_GLOW_HEIGHT);
        failLED.render(guiGraphics,leftPos + 15, topPos + 47 + (baseOffset * 2), LED_GLOW_WIDTH, LED_GLOW_HEIGHT);
    }
}