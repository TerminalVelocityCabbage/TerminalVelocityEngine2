package com.terminalvelocitycabbage.engine.client.renderer.model;

import com.terminalvelocitycabbage.engine.registry.Identifier;

public class Model {

    Identifier meshIdentifier;
    Identifier textureIdentifier;

    public Model(Identifier meshIdentifier, Identifier textureIdentifier) {
        this.meshIdentifier = meshIdentifier;
        this.textureIdentifier = textureIdentifier;
    }

    public Identifier getMeshIdentifier() {
        return meshIdentifier;
    }

    public void setMeshIdentifier(Identifier meshIdentifier) {
        this.meshIdentifier = meshIdentifier;
    }

    public Identifier getTextureIdentifier() {
        return textureIdentifier;
    }

    public void setTextureIdentifier(Identifier textureIdentifier) {
        this.textureIdentifier = textureIdentifier;
    }
}
