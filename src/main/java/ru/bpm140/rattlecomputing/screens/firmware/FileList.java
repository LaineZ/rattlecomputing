package ru.bpm140.rattlecomputing.screens.firmware;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ObjectSelectionList;

public class FileList extends ObjectSelectionList<FileEntry> {

    public FileList(Minecraft mc,
                    int width,
                    int height,
                    int top,
                    int elementHeight) {

        super(mc, width, height, top, elementHeight);
    }

    @Override
    public int getRowWidth() {
        return this.width - 20;
    }


    public void addFileEntry(FileEntry entry) {
        this.addEntry(entry);
    }

    @Override
    protected int getScrollbarPosition() {
        return this.width - 6;
    }
}