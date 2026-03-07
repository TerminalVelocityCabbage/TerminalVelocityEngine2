package com.terminalvelocitycabbage.templates.ecs.components;

import com.terminalvelocitycabbage.engine.client.renderer.Projection;
import com.terminalvelocitycabbage.engine.ecs.Component;
import com.terminalvelocitycabbage.engine.util.Transformation;
import org.joml.Matrix4f;

public abstract class CameraComponent implements Component {

    private final Projection projection;
    private final Matrix4f viewMatrix = new Matrix4f();

    public CameraComponent(Projection projection) {
        this.projection = projection;
    }

    @Override
    public void setDefaults() {
        viewMatrix.identity();
    }

    public Matrix4f getProjectionMatrix() {
        return projection.getProjectionMatrix();
    }

    public Matrix4f getViewMatrix(Transformation translation) {
        return viewMatrix.identity().translate(translation.getPosition());
    }

    public abstract void updateProjectionMatrix(int width, int height);

    public Projection getProjection() {
        return projection;
    }
}
