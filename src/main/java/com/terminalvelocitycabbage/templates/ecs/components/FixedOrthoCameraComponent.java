package com.terminalvelocitycabbage.templates.ecs.components;

import com.terminalvelocitycabbage.engine.client.renderer.Projection;
import com.terminalvelocitycabbage.engine.ecs.Component;
import com.terminalvelocitycabbage.engine.ecs.Entity;
import org.joml.Matrix4f;

public class FixedOrthoCameraComponent extends CameraComponent {

    private final Matrix4f viewMatrix = new Matrix4f();

    public FixedOrthoCameraComponent() {
        super(new Projection(Projection.Type.ORTHOGONAL, 0.1f, 1000f));
    }

    @Override
    public void setDefaults() {
        viewMatrix.identity();
    }

    public Matrix4f getProjectionMatrix() {
        return getProjection().getProjectionMatrix();
    }

    public void updateProjectionMatrix(int width, int height) {
        getProjection().updateProjectionMatrix(width, height);
    }
}
