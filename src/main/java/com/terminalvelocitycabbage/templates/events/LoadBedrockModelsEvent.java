package com.terminalvelocitycabbage.templates.events;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.io.ConfigParser;
import com.electronwill.nightconfig.json.JsonFormat;
import com.terminalvelocitycabbage.engine.TerminalVelocityEngine;
import com.terminalvelocitycabbage.engine.client.renderer.elements.VertexFormat;
import com.terminalvelocitycabbage.engine.client.renderer.model.Mesh;
import com.terminalvelocitycabbage.engine.client.renderer.model.bedrock.BedrockMesh;
import com.terminalvelocitycabbage.engine.client.renderer.model.bedrock.BedrockModelLoader;
import com.terminalvelocitycabbage.engine.event.Event;
import com.terminalvelocitycabbage.engine.filesystem.GameFileSystem;
import com.terminalvelocitycabbage.engine.filesystem.resources.Resource;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceCategory;
import com.terminalvelocitycabbage.engine.registry.Identifier;

import java.util.Map;

public class LoadBedrockModelsEvent extends Event {

    public static final Identifier EVENT = TerminalVelocityEngine.identifierOf("LoadBedrockModelsEvent");

    private final GameFileSystem fileSystem;

    public LoadBedrockModelsEvent(GameFileSystem fileSystem) {
        super(EVENT);
        this.fileSystem = fileSystem;
    }

    public GameFileSystem getFileSystem() {
        return fileSystem;
    }

    public void cache(Map<Identifier, Mesh> cache, Identifier resourceIdentifier, VertexFormat vertexFormat) {
        ConfigParser<Config> parser = JsonFormat.minimalInstance().createParser();
        Resource resource = fileSystem.getResource(ResourceCategory.MODEL, resourceIdentifier);
        var bedrockMesh = new BedrockMesh(BedrockModelLoader.loadGeometry(resource, parser));
        cache.put(resourceIdentifier, new Mesh(vertexFormat, bedrockMesh));
    }
}
