package com.terminalvelocitycabbage.templates.ecs.components;

import com.terminalvelocitycabbage.engine.client.renderer.model.Model;
import com.terminalvelocitycabbage.engine.ecs.Component;

public class ModelComponent implements Component {

    Model model;

    @Override
    public void setDefaults() {
        model = null;
    }

    @Override
    public void cleanup() {
        model.cleanup();
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }
}
