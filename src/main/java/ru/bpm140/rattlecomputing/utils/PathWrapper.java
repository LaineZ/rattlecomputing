package ru.bpm140.rattlecomputing.utils;

import java.nio.file.Files;
import java.nio.file.Path;

public class PathWrapper {
    public Path path;
    public final boolean isRoot;

    public PathWrapper(Path p, boolean isRoot) {
        path = p;
        this.isRoot = isRoot;
    }

    public boolean isDirectory() {
        if (this.path != null) {
            return Files.isDirectory(this.path);
        } else {
            return false;
        }
    }

    public String filenameDisplay() {
        if (path.getFileName() == null) {
            return path.toString();
        } else {
            return path.getFileName().toString();
        }
    }
}
