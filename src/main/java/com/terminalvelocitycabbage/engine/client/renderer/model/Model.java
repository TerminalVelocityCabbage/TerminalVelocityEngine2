package com.terminalvelocitycabbage.engine.client.renderer.model;

import com.terminalvelocitycabbage.engine.client.renderer.materials.TextureCache;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import org.joml.Matrix4f;

public record Model(Mesh compiledMesh, Identifier textureIdentifier, Skeleton skeleton) {

    public Model(Mesh compiledMesh, Identifier textureIdentifier) {
        this(compiledMesh, textureIdentifier, null);
    }

    public void render(TextureCache textureCache) {
        bindTexture(textureCache);
        bind();
        draw();
    }

    public void bindTexture(TextureCache textureCache) {
        if (textureIdentifier() != null) {
            textureCache.getTexture(textureIdentifier()).bind();
        }
    }

    public void bind() {
        if (compiledMesh() != null) {
            compiledMesh().bind();
        }
    }

    public void draw() {
        if (compiledMesh() != null) {
            compiledMesh().draw();
        }
    }
}
