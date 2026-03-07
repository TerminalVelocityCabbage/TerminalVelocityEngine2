package com.terminalvelocitycabbage.templates.ecs.components;

import com.terminalvelocitycabbage.engine.client.renderer.model.Mesh;
import com.terminalvelocitycabbage.engine.ecs.Component;

public class MeshComponent implements Component {

    Mesh mesh;

    @Override
    public void setDefaults() {
        this.mesh = null;
    }

    @Override
    public void cleanup() {
        mesh.cleanup();
    }

    public Mesh getMesh() {
        return mesh;
    }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }
}
