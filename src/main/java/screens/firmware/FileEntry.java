package screens.firmware;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import ru.bpm140.rattlecomputing.utils.PathWrapper;

import java.nio.file.Files;

public class FileEntry extends ObjectSelectionList.Entry<FileEntry> {
    public final PathWrapper file;
    private static final ResourceLocation DIRECTORY_ICON =
            ResourceLocation.fromNamespaceAndPath("rattlecomputing", "textures/gui/directory.png");

    private static final ResourceLocation FILE_ICON =
            ResourceLocation.fromNamespaceAndPath("rattlecomputing", "textures/gui/file.png");


    public FileEntry(PathWrapper file) {
        this.file = file;
    }


    @Override
    public void render(GuiGraphics gfx,
                       int index,
                       int y,
                       int x,
                       int width,
                       int height,
                       int mouseX,
                       int mouseY,
                       boolean hovered,
                       float partialTick) {

        int iconX = x + 2;

        gfx.drawString(
                Minecraft.getInstance().font,
                file.filenameDisplay(),
                x + 20,
                y + 4,
                0xFFFFFF
        );

        gfx.blit(file.isDirectory() ? DIRECTORY_ICON : FILE_ICON,
                iconX,
                y - 1,
                16, 16,
                0, 0,
                16, 16,
                16, 16);
    }

    @Override
    public Component getNarration() {
        return Component.literal(file.toString());
    }
}