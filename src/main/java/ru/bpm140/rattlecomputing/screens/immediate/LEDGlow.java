package ru.bpm140.rattlecomputing.screens.immediate;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Matrix4f;
import ru.bpm140.rattlecomputing.Shaders;

public class LEDGlow {
    public float intensity = 1.0f;
    public float[] color = new float[3];

    public static LEDGlow RED() {
        var led = new LEDGlow();
        led.color[0] = 1.0f;
        return led;
    }

    public static LEDGlow BLUE() {
        var led = new LEDGlow();
        led.color[0] = 0.2f;
        led.color[1] = 0.2f;
        led.color[2] = 0.8f;
        return led;
    }

    public static LEDGlow GREEN() {
        var led = new LEDGlow();
        led.color[1] = 1.0f;
        return led;
    }

    public void render(GuiGraphics g, int x, int y, int sizeX, int sizeY) {
        float x1 = x;
        float y1 = y;
        float x2 = x + sizeX;
        float y2 = y + sizeY;

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE
        );

        Matrix4f mat = g.pose().last().pose();

        RenderSystem.setShader(() -> Shaders.LED_GLOW);
        var ledColor = Shaders.LED_GLOW.getUniform("LedColor");
        var ledIntensity = Shaders.LED_GLOW.getUniform("Intensity");
        ledColor.set(color[0], color[1], color[2]);
        ledIntensity.set(intensity);

        BufferBuilder buffer = Tesselator.getInstance()
                .begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        buffer.addVertex(mat, x1, y2, 0).setUv(0, 1);
        buffer.addVertex(mat, x2, y2, 0).setUv(1, 1);
        buffer.addVertex(mat, x2, y1, 0).setUv(1, 0);
        buffer.addVertex(mat, x1, y1, 0).setUv(0, 0);

        BufferUploader.drawWithShader(buffer.buildOrThrow());

        RenderSystem.disableBlend();
    }
}
