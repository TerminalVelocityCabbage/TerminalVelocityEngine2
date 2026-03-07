package com.terminalvelocitycabbage.engine.filesystem.selector;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.Optional;

import static org.lwjgl.system.MemoryStack.stackPush;

public final class FileDialogs {

    private FileDialogs() {}

    /**
     * Opens a file picker dialog.
     */
    public static Optional<Path> openFile(
            String title,
            Path defaultPath,
            String... filters // ex: "*.png", "*.jpg"
    ) {
        try (MemoryStack stack = stackPush()) {

            PointerBuffer filterBuffer = null;
            if (filters != null && filters.length > 0) {
                filterBuffer = stack.mallocPointer(filters.length);
                for (String f : filters) {
                    ByteBuffer buf = stack.UTF8(f);
                    filterBuffer.put(buf);
                }
                filterBuffer.flip();
            }

            String result = TinyFileDialogs.tinyfd_openFileDialog(
                    title,
                    defaultPath != null ? defaultPath.toString() : null,
                    filterBuffer,
                    null,
                    false
            );

            return result == null ? Optional.empty() : Optional.of(Path.of(result));
        }
    }

    /**
     * Opens a save file dialog.
     */
    public static Optional<Path> saveFile(
            String title,
            Path defaultPath,
            String... filters
    ) {
        try (MemoryStack stack = stackPush()) {

            PointerBuffer filterBuffer = null;
            if (filters != null && filters.length > 0) {
                filterBuffer = stack.mallocPointer(filters.length);
                for (String f : filters) {
                    filterBuffer.put(stack.UTF8(f));
                }
                filterBuffer.flip();
            }

            String result = TinyFileDialogs.tinyfd_saveFileDialog(
                    title,
                    defaultPath != null ? defaultPath.toString() : null,
                    filterBuffer,
                    null
            );

            return result == null ? Optional.empty() : Optional.of(Path.of(result));
        }
    }

    /**
     * Opens a folder selection dialog.
     */
    public static Optional<Path> selectFolder(
            String title,
            Path defaultPath
    ) {
        String result = TinyFileDialogs.tinyfd_selectFolderDialog(
                title,
                defaultPath != null ? defaultPath.toString() : null
        );

        return result == null ? Optional.empty() : Optional.of(Path.of(result));
    }
}
