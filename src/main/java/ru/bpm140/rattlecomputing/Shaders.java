package ru.bpm140.rattlecomputing;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

import java.io.IOException;

@EventBusSubscriber(modid = Rattlecomputing.MODID, value = Dist.CLIENT)
public class Shaders {
    public static ShaderInstance LED_GLOW;

    @SubscribeEvent
    public static void registerShaders(RegisterShadersEvent event) throws IOException {
        event.registerShader(
                new ShaderInstance(
                        event.getResourceProvider(),
                        ResourceLocation.fromNamespaceAndPath("rattlecomputing", "glow"),
                        DefaultVertexFormat.POSITION_TEX
                ),
                shader -> LED_GLOW = shader
        );
    }
}