package com.terminalvelocitycabbage.templates.ecs.components;

import com.terminalvelocitycabbage.engine.ecs.Component;
import com.terminalvelocitycabbage.engine.registry.Identifier;

public class MaterialComponent implements Component {

    Identifier texture;

    @Override
    public void setDefaults() {
        texture = null;
    }

    public Identifier getTexture() {
        return texture;
    }

    public void setTexture(Identifier texture) {
        this.texture = texture;
    }
}
