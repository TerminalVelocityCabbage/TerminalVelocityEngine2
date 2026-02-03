package com.terminalvelocitycabbage.templates.ecs.components;

import com.terminalvelocitycabbage.engine.client.renderer.Projection;
import com.terminalvelocitycabbage.engine.ecs.Component;
import com.terminalvelocitycabbage.engine.ecs.Entity;
import org.joml.Matrix4f;

public class FixedOrthoCameraComponent implements Component {

    private static final Projection ORTHO = new Projection(Projection.Type.ORTHOGONAL, 0.1f, 1000f);
    private final Matrix4f viewMatrix = new Matrix4f();

    @Override
    public void setDefaults() {
        viewMatrix.identity();
    }

    @Override
    public void cleanup() {
        Component.super.cleanup();
    }


    public Matrix4f getProjectionMatrix() {
        return ORTHO.getProjectionMatrix();
    }

    public Matrix4f getViewMatrix(Entity entity) {

        var currentPosition = entity.getComponent(PositionComponent.class).getPosition();

        return viewMatrix.identity().translate(currentPosition);
    }

    public void updateProjectionMatrix(int width, int height) {
        ORTHO.updateProjectionMatrix(width, height);
    }
}
