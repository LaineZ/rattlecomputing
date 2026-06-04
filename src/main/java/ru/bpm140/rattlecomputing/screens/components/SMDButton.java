package ru.bpm140.rattlecomputing.screens.components;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class SMDButton extends Button {
    public enum State {
        IDLE,
        HOVER,
        ACTIVE
    }

    private final ResourceLocation texture;
    private static final int TEXTURE_SIZE_W = 10;
    private static final int TEXTURE_SIZE_H = 21;
    private static final int STATES_COUNT = 3;

    public SMDButton(int x, int y, OnPress onPress) {
        super(x, y, TEXTURE_SIZE_W, TEXTURE_SIZE_H / STATES_COUNT, Component.empty(), onPress, DEFAULT_NARRATION);
        this.texture = ResourceLocation.fromNamespaceAndPath("rattlecomputing", "textures/gui/sprites/smd_buttons.png");
    }

    @Override
    protected void renderWidget(GuiGraphics gg, int mouseX, int mouseY, float partialTick) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, texture);

        var state = State.IDLE;

        if (this.isHoveredOrFocused()) {
            state = State.HOVER;
        }

        if (this.isHovered && Minecraft.getInstance().mouseHandler.isLeftPressed()) {
            state = State.ACTIVE;
        }

        int u = 0;
        int v = state.ordinal() * (TEXTURE_SIZE_H / STATES_COUNT);

        gg.blit(texture, this.getX(), this.getY(), u, v, this.width, this.height, TEXTURE_SIZE_W, TEXTURE_SIZE_H);
    }
}
