package com.terminalvelocitycabbage.engine.config;

import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.core.file.FileNotFoundAction;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.filesystem.GameFileSystem;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceCategory;
import com.terminalvelocitycabbage.engine.registry.Identifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class TVConfig {

    /**
     * Gets the referenced config file from the disk or creates and returns a new one from a default config, from within
     * a resource.
     *
     * @param fileSystem The filesystem that this config should exist in
     * @param resourceIdentifier The resource identifier that refers to this config or default config
     * @return A FileConfig reference to this config file
     */
    public static FileConfig getOrCreateFileConfig(GameFileSystem fileSystem, Identifier resourceIdentifier) {

        String configDir = "configs/" + resourceIdentifier.namespace();
        FileConfig fileConfig = null;
        try {
            //init directory
            Files.createDirectories(Paths.get(configDir));
            fileConfig = FileConfig
                    .builder(configDir + "/" + resourceIdentifier.name())
                    .onFileNotFound(FileNotFoundAction.copyData(Objects.requireNonNull(fileSystem.getResource(ResourceCategory.DEFAULT_CONFIG, resourceIdentifier).openStream())))
                    .build();
            fileConfig.load();
            fileConfig.save();
        } catch (IOException e) {
            Log.crash("Could not create config file directory", new RuntimeException(e));
        }

        return fileConfig;
    }
}
