package screens.firmware;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import ru.bpm140.rattlecomputing.utils.PathWrapper;

import java.nio.file.Path;

public class UpToDirectoryEntry extends FileEntry {
    private static final String TEXT = "< Up to directory >";

    public UpToDirectoryEntry(PathWrapper file) {
        super(file);
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

        gfx.drawString(Minecraft.getInstance().font, TEXT, x + 4, y + 4, 0xFFFFFF);
    }

    @Override
    public Component getNarration() {
        return Component.literal(TEXT);
    }
}