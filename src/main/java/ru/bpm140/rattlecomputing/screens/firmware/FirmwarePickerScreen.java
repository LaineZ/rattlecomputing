package ru.bpm140.rattlecomputing.screens.firmware;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import ru.bpm140.rattlecomputing.utils.PathWrapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FirmwarePickerScreen extends Screen {
    public interface FilePickerHandler {
        boolean onSelect(Path path);
    }

    private Path directory;
    private FileList list;
    private boolean browsingRoots;
    private EditBox pathBox;
    private FilePickerHandler callback;

    public FirmwarePickerScreen(Path initialDirectory, FilePickerHandler callback) {
        super(Component.literal("Select firmware ELF"));
        this.directory = initialDirectory;
        this.callback = callback;
    }

    private void loadRoots() {
        for (File root : File.listRoots()) {
            list.addFileEntry(new FileEntry(new PathWrapper(root.toPath(), true), this::handleOpen));
        }
    }

    private void handleOpen() {
        var selected = list.getSelected();

        if (selected != null) {
            if (selected instanceof UpToDirectoryEntry) {
                goUp();
            } else {
                onOpen(selected.file);
            }
        }
    }


    private void loadFiles() {
        list.children().clear();
        list.setSelected(null);
        list.setScrollAmount(0);

        try {
            if (browsingRoots) {
                loadRoots();
                return;
            }
            Files.createDirectories(directory);
            Path parent = directory.getParent();
            list.addFileEntry(
                    new UpToDirectoryEntry(new PathWrapper(directory, false), this::handleOpen)
            );

            Files.list(directory).forEach(p -> list.addFileEntry(new FileEntry(new PathWrapper(p, false), this::handleOpen)));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void goUp() {
        if (browsingRoots) return;
        Path parent = directory.getParent();
        if (parent == null) {
            browsingRoots = true;
            loadFiles();
            return;
        }

        directory = parent;
        pathBox.setValue(directory.toString());
        loadFiles();
    }

    private void onOpen(PathWrapper file) {
        if (!Files.isDirectory(file.path)) {
            boolean isOk = callback.onSelect(file.path);
            if (isOk) {
                this.onClose();
            }
        } else {
            if (file.isRoot) {
                browsingRoots = false;
            }

            this.directory = file.path;
            loadFiles();
        }

        pathBox.setValue(directory.toString());
    }

    private void onPathTyped(String value) {
        try {
            Path p = Path.of(value);

            if (Files.exists(p) && Files.isDirectory(p)) {
                this.directory = p;
                this.browsingRoots = false;
                loadFiles();
            }

        } catch (Exception ignored) {}
    }

    @Override
    protected void init() {
        list = new FileList(this.minecraft, this.width, this.height - 55, 20, 18);
        pathBox = new EditBox(
                Minecraft.getInstance().font,
                10,
                this.height - 26,
                300,
                20,
                Component.literal("Path")
        );
        this.addRenderableWidget(list);
        pathBox.setMaxLength(4096);

        loadFiles();

        addRenderableWidget(Button.builder(Component.literal("Open"), btn -> {
            handleOpen();
        }).bounds(this.width - 200, this.height - 26, 90, 20).build());

        addRenderableWidget(Button.builder(Component.literal("Cancel"), btn -> {
            this.onClose();
        }).bounds(this.width - 100, this.height - 26, 90, 20).build());

        pathBox.setValue(directory.toString());
        pathBox.setResponder(this::onPathTyped);
        this.addRenderableWidget(pathBox);
    }
}