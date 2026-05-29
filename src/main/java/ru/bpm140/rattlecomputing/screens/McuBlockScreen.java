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
import ru.bpm140.rattlecomputing.menus.McuBlockMenu;
import ru.bpm140.rattlecomputing.screens.components.SMDImageButton;

@OnlyIn(Dist.CLIENT)
public class McuBlockScreen extends AbstractContainerScreen<McuBlockMenu> {
    private static final ResourceLocation CONTAINER_TEXTURE = ResourceLocation.fromNamespaceAndPath("rattlecomputing", "textures/gui/mcu_container.png");
    private DisplayTexture texture;

    public McuBlockScreen(McuBlockMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
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

        guiGraphics.blit(
                CONTAINER_TEXTURE,
                leftPos, topPos,
                0, 0,
                imageWidth, imageHeight
        );
    }
}