package screens;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import ru.bpm140.rattlecomputing.DisplayTexture;
import ru.bpm140.rattlecomputing.SelfDestructPacket;
import ru.bpm140.rattlecomputing.blockentities.McuBlockEntity;
import ru.bpm140.rattlecomputing.menus.McuBlockMenu;

@OnlyIn(Dist.CLIENT)
public class McuBlockScreen extends AbstractContainerScreen<McuBlockMenu> {

    private static final WidgetSprites POWER_BUTTON_SPRITES =
            new WidgetSprites(
                    ResourceLocation.fromNamespaceAndPath("rattlecomputing", "widget/power"),
                    ResourceLocation.fromNamespaceAndPath("rattlecomputing", "widget/power_on"),
                    ResourceLocation.fromNamespaceAndPath("rattlecomputing", "widget/power_hover")
            );
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


        this.addRenderableWidget(
                new ImageButton(
                        leftPos + 10,
                        topPos + 10,
                        20,
                        20,
                        POWER_BUTTON_SPRITES,
                        btn -> onClick()
                )
        );
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(g, mouseX, mouseY, partialTick);
        super.render(g, mouseX, mouseY, partialTick);
        this.renderTooltip(g, mouseX, mouseY);

        McuBlockEntity be = menu.be;
        // TODO: Reactive dirty tracking
        texture.update(be.pixels);

        int x = leftPos + 25;
        int y = topPos + 3;

        //g.blit(texture.getId(), x, y, 0, 0, 128, 64, 128, 64);
    }

    private void onClick() {
        Minecraft.getInstance().getConnection().send(
                new SelfDestructPacket(menu.be.getBlockPos())
        );
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