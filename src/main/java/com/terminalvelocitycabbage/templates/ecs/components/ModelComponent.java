package com.terminalvelocitycabbage.templates.ecs.components;

import com.terminalvelocitycabbage.engine.ecs.Component;
import com.terminalvelocitycabbage.engine.registry.Identifier;

public class ModelComponent implements Component {

    Identifier model;

    @Override
    public void setDefaults() {
        this.model = null;
    }

    public Identifier getModel() {
        return model;
    }

    public void setModel(Identifier mesh) {
        this.model = mesh;
    }
}
