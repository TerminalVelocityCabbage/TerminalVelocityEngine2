package com.terminalvelocitycabbage.engine.client.renderer.model;

import com.terminalvelocitycabbage.engine.client.renderer.materials.TextureCache;
import com.terminalvelocitycabbage.engine.registry.Identifier;

public record Model(Mesh compiledMesh, Identifier atlasIdentifier) {

    public void render(TextureCache textureCache) {
        if (atlasIdentifier() != null) {
            textureCache.getTexture(atlasIdentifier()).bind();
        }
        if (compiledMesh() != null) {
            compiledMesh().render();
        }
    }
}
