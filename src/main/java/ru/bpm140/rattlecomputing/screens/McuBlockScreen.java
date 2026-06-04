package ru.bpm140.rattlecomputing.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import ru.bpm140.rattlecomputing.DisplayTexture;
import ru.bpm140.rattlecomputing.menus.McuBlockMenu;
import ru.bpm140.rattlecomputing.network.CPUClientStore;
import ru.bpm140.rattlecomputing.network.packets.SetCPUStatusPacket;
import ru.bpm140.rattlecomputing.screens.components.SMDButton;
import ru.bpm140.rattlecomputing.screens.immediate.LEDGlow;
import ru.bpm140.rottenmangal.CPUSnapshot;
import ru.bpm140.rottenmangal.CPUStatus;

@OnlyIn(Dist.CLIENT)
public class McuBlockScreen extends AbstractContainerScreen<McuBlockMenu> implements CPUClientStore.CpuListener {
    private static final ResourceLocation CONTAINER_TEXTURE = ResourceLocation.fromNamespaceAndPath("rattlecomputing",
            "textures/gui/mcu_container.png");
    private final DisplayTexture displayTexture = new DisplayTexture(40, 34, "internaldisplay");

    private static final int CONTAINER_TEXTURE_WIDTH = 176;
    private static final int CONTAINER_TEXTURE_HEIGHT = 166;
    private static final int LED_GLOW_WIDTH = 12;
    private static final int LED_GLOW_HEIGHT = 7;

    private CPUSnapshot snapshot = new CPUSnapshot();

    private final LEDGlow LED = LEDGlow.GREEN();
    private final LEDGlow failLED = LEDGlow.RED();
    private final LEDGlow runLED = LEDGlow.GREEN();

    public McuBlockScreen(McuBlockMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Override
    protected void init() {
        super.init();

        this.addRenderableWidget(new SMDButton(leftPos + 16, topPos + 32, btn ->
                PacketDistributor.sendToServer(new SetCPUStatusPacket(menu.mcuBlockEntity.getBlockPos(), SetCPUStatusPacket.Action.POWER))));
        this.addRenderableWidget(new SMDButton(leftPos + 16, topPos + 40, btn ->
                PacketDistributor.sendToServer(new SetCPUStatusPacket(menu.mcuBlockEntity.getBlockPos(), SetCPUStatusPacket.Action.RESET))));


        LED.intensity = 0.0f;

        CPUClientStore.subscribe(this);
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

        if (snapshot.state == CPUStatus.CPUState.RUNNING) {
            runLED.intensity = 1.0f;
        } else {
            runLED.intensity = 0.0f;
        }

        if (snapshot.state == CPUStatus.CPUState.FAULT) {
            failLED.intensity = 1.0f;
        } else {
            failLED.intensity = 0.0f;
        }

        // TODO: Texture update
        guiGraphics.blit(displayTexture.getId(), leftPos + 109, topPos + 16, 0, 0,
                displayTexture.getWidth(), displayTexture.getHeight(), displayTexture.getWidth(), displayTexture.getHeight());
    }

    @Override
    public void removed() {
         snapshot = new CPUSnapshot();
        CPUClientStore.unsubscribe(this);
    }

    @Override
    public void onCpuUpdate(BlockPos pos, CPUStatus.CPUState state) {
        var pizda = menu.mcuBlockEntity.getBlockPos();
        if (pos.equals(pizda)) {
            this.snapshot.state = state;
            CPUClientStore.remove(pos);
        }
    }
}