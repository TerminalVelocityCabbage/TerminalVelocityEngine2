package com.terminalvelocitycabbage.engine.client.renderer.model;

import com.terminalvelocitycabbage.engine.client.renderer.materials.Atlas;
import com.terminalvelocitycabbage.engine.client.renderer.materials.TextureCache;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.registry.Registry;

import java.util.HashMap;
import java.util.Map;

public class MeshCache {

    Map<Identifier, Mesh> modelMeshMap;

    public MeshCache(Registry<Model> modelRegistry, Registry<Mesh> meshRegistry, TextureCache textureCache) {
        this.modelMeshMap = new HashMap<>();

        modelRegistry.getRegistryContents().forEach((identifier, model) -> {
            Log.info("Loading mesh " + model.getMeshIdentifier() + " for model " + identifier);
            Log.info(meshRegistry);
            modelMeshMap.put(identifier, new Mesh(meshRegistry.get(model.getMeshIdentifier())));
        });

        modelRegistry.getRegistryContents().forEach((identifier, model) -> {
            var textureIdentifier = model.getTextureIdentifier();
            if (textureCache.getTexture(textureIdentifier) instanceof Atlas atlas) {
                var mesh = modelMeshMap.get(identifier);
                mesh.transformUVsByAtlas(atlas, textureIdentifier);
            }
        });
    }

    public Mesh getMesh(Identifier modelIdentifier) {
        return modelMeshMap.get(modelIdentifier);
    }

    public void cleanup() {
        modelMeshMap.values().forEach(Mesh::cleanup);
    }
}
