package ru.bpm140.rattlecomputing;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;

public class DisplayTexture {
    private final NativeImage image;
    private final DynamicTexture texture;
    private final ResourceLocation textureId;
    private final boolean isDirty = false;

    public DisplayTexture() {
        this.image = new NativeImage(128, 64, false);
        this.texture = new DynamicTexture(image);

        this.textureId = Minecraft.getInstance()
                .getTextureManager()
                .register("lcd", texture);
    }

    public ResourceLocation getId() {
        return textureId;
    }

    public void update(byte[] pixels) {
        for (int y = 0; y < 64; y++) {
            for (int x = 0; x < 128; x++) {

                int i = x + y * 128;
                int v = pixels[i] & 0xFF;

                int color = 0xFF000000 | (v << 16) | (v << 8) | v;

                image.setPixelRGBA(x, y, color);
            }
        }

        texture.upload();
    }
}
