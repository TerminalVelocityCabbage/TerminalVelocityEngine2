package com.terminalvelocitycabbage.engine.client.renderer.model;

import com.terminalvelocitycabbage.engine.client.renderer.materials.Atlas;
import com.terminalvelocitycabbage.engine.client.renderer.materials.TextureCache;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.registry.Registry;

import java.util.ArrayList;
import java.util.List;

public class MeshCache {

    private final Registry<Model> modelRegistry;

    public MeshCache(Registry<ModelConfig> modelConfigRegistry, Registry<Model> modelRegistry, Registry<Mesh> meshRegistry, TextureCache textureCache) {
        this.modelRegistry = modelRegistry;

        modelConfigRegistry.getRegistryContents().forEach((identifier, config) -> {
            List<Mesh> meshesToMerge = new ArrayList<>();
            Atlas commonAtlas = null;

            for (var pair : config.meshTexturePairs()) {
                var sourceMesh = meshRegistry.get(pair.meshIdentifier());
                if (sourceMesh == null) {
                    Log.crash("Mesh " + pair.meshIdentifier() + " not found for model " + identifier);
                }
                var mesh = new Mesh(sourceMesh);
                var textureIdentifier = pair.textureIdentifier();
                var texture = textureCache.getTexture(textureIdentifier);

                if (texture instanceof Atlas atlas) {
                    if (commonAtlas != null && commonAtlas != atlas) {
                        Log.crash("All textures for model " + identifier + " must be on the same atlas! Found: " + commonAtlas + " and " + atlas);
                    }
                    commonAtlas = atlas;
                    mesh.transformUVsByAtlas(atlas, textureIdentifier);
                } else if (config.meshTexturePairs().size() > 1) {
                    Log.crash("All textures for model " + identifier + " must be on the same atlas if there are multiple! Texture " + textureIdentifier + " is not on an atlas.");
                }

                meshesToMerge.add(mesh);
            }

            var compiledMesh = Mesh.of(meshesToMerge);
            var atlasIdentifier = config.meshTexturePairs().get(0).textureIdentifier();
            modelRegistry.register(identifier, new Model(compiledMesh, atlasIdentifier), true);
        });
    }

    public Mesh getMesh(Identifier modelIdentifier) {
        var model = modelRegistry.get(modelIdentifier);
        return model == null ? null : model.compiledMesh();
    }

    public void cleanup() {
        modelRegistry.getRegistryContents().values().forEach(model -> {
            if (model.compiledMesh() != null) {
                model.compiledMesh().cleanup();
            }
        });
    }
}
