package com.terminalvelocitycabbage.templates.events;

import com.terminalvelocitycabbage.engine.TerminalVelocityEngine;
import com.terminalvelocitycabbage.engine.event.Event;
import com.terminalvelocitycabbage.engine.filesystem.GameFileSystem;
import com.terminalvelocitycabbage.engine.filesystem.resources.Resource;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceCategory;
import com.terminalvelocitycabbage.engine.registry.Identifier;

import java.util.HashMap;
import java.util.Map;

public class ConfigureTexturesEvent extends Event {

    public static final Identifier EVENT = TerminalVelocityEngine.identifierOf("ConfigureTexturesEvent");

    private GameFileSystem fileSystem;

    private final Map<Identifier, Map<Identifier, Resource>> texturesToCompileToAtlas;
    private final Map<Identifier, Resource> singleTextures;

    public ConfigureTexturesEvent(GameFileSystem fileSystem) {
        super(EVENT);
        this.fileSystem = fileSystem;
        texturesToCompileToAtlas = new HashMap<>();
        singleTextures = new HashMap<>();
    }

    public Identifier registerAtlas(Identifier atlasIdentifier) {
        texturesToCompileToAtlas.putIfAbsent(atlasIdentifier, new HashMap<>());
        return atlasIdentifier;
    }

    public void addTexture(Identifier textureIdentifier, Identifier... atlasIdentifiers) {
        if (atlasIdentifiers.length == 0) {
            singleTextures.putIfAbsent(textureIdentifier, fileSystem.getResource(ResourceCategory.TEXTURE, textureIdentifier));
        } else {
            for (Identifier atlasIdentifier : atlasIdentifiers) {
                texturesToCompileToAtlas.get(atlasIdentifier).putIfAbsent(textureIdentifier, fileSystem.getResource(ResourceCategory.TEXTURE, textureIdentifier));
            }
        }
    }

    public Map<Identifier, Map<Identifier, Resource>> getTexturesToCompileToAtlas() {
        return texturesToCompileToAtlas;
    }

    public Map<Identifier, Resource> getSingleTextures() {
        return singleTextures;
    }
}
