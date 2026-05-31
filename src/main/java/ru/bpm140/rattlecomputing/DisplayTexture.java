package ru.bpm140.rattlecomputing;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class DisplayTexture {
    private final NativeImage image;
    private final DynamicTexture texture;
    private final ResourceLocation textureId;
    private final boolean isDirty = false;

    private final int width;
    private final int height;

    public DisplayTexture(int width, int height, String registration) {
        this.width = width;
        this.height = height;

        this.image = new NativeImage(this.width, this.height, false);
        this.texture = new DynamicTexture(image);

        this.textureId = Minecraft.getInstance()
                .getTextureManager()
                .register(registration, texture);
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public ResourceLocation getId() {
        return textureId;
    }

    public void update(byte[] pixels) {
        if (pixels.length != width * height) {
            return;
        }

        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                int i = x + y * this.width;
                int v = pixels[i] & 0xFF;
                int color = 0xFF000000 | (v << 16) | (v << 8) | v;
                image.setPixelRGBA(x, y, color);
            }
        }

        texture.upload();
    }
}
