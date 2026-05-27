package screens.firmware;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import ru.bpm140.rattlecomputing.utils.PathWrapper;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FirmwarePickerScreen extends Screen {
    private Path directory;
    private FileList list;
    private boolean browsingRoots;
    public FirmwarePickerScreen(Path initialDirectory) {
        super(Component.literal("Select firmware ELF"));
        this.directory = initialDirectory;
    }

    private void loadRoots() {
        for (File root : File.listRoots()) {
            list.addFileEntry(new FileEntry(new PathWrapper(root.toPath(), true)));
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
                    new UpToDirectoryEntry(new PathWrapper(directory, false))
            );

            Files.list(directory).forEach(p -> list.addFileEntry(new FileEntry(new PathWrapper(p, false))));

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
        loadFiles();
    }

    private void onOpen(PathWrapper file) {
        if (!Files.isDirectory(file.path)) {
            try {
                byte[] data = Files.readAllBytes(file.path);
                System.out.println("selected file: " + file.path);
                this.onClose();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (file.isRoot) {
                browsingRoots = false;
            }

            this.directory = file.path;
            loadFiles();
        }
    }

    @Override
    protected void init() {
        list = new FileList(this.minecraft, this.width, this.height - 55, 20, 18);
        this.addRenderableWidget(list);

        loadFiles();

        int centerX = this.width / 2;

        addRenderableWidget(Button.builder(Component.literal("Open"), btn -> {
            var selected = list.getSelected();

            if (selected != null) {
                if (selected instanceof UpToDirectoryEntry) {
                    goUp();
                } else {
                    onOpen(selected.file);
                }
            }
        }).bounds(centerX - 100, this.height - 26, 90, 20).build());

        addRenderableWidget(Button.builder(Component.literal("Cancel"), btn -> {
            this.onClose();
        }).bounds(centerX + 10, this.height - 26, 90, 20).build());
    }
}