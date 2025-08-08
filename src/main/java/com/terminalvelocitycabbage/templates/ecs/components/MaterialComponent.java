package com.terminalvelocitycabbage.templates.ecs.components;

import com.terminalvelocitycabbage.engine.client.renderer.materials.Atlas;
import com.terminalvelocitycabbage.engine.ecs.Component;
import com.terminalvelocitycabbage.engine.registry.Identifier;

public class MaterialComponent implements Component {

    Identifier texture;
    Atlas atlas;

    @Override
    public void setDefaults() {
        setTexture(null);
    }

    public Identifier getTexture() {
        return texture;
    }

    public void setTexture(Identifier texture) {
        this.texture = texture;
    }

    public void setTexture(Identifier texture, Atlas atlas) {
        this.texture = texture;
        this.atlas = atlas;
    }
}
