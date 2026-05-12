package com.terminalvelocitycabbage.editor.ecs.components;

import com.terminalvelocitycabbage.editor.hints.EditorHint;
import com.terminalvelocitycabbage.engine.client.renderer.Projection;
import com.terminalvelocitycabbage.templates.ecs.components.CameraComponent;
import com.terminalvelocitycabbage.engine.util.Transformation;
import org.joml.Matrix4f;

@EditorHint.ComponentName(name = "Editor Camera")
public class EditorCameraComponent extends CameraComponent {

    public EditorCameraComponent() {
        super(new Projection(Projection.Type.PERSPECTIVE, 70, 0.1f, 1000f));
    }

    @Override
    public void updateProjectionMatrix(int width, int height) {
        getProjection().updateProjectionMatrix(width, height);
    }

    @Override
    public Matrix4f getViewMatrix(Transformation transformation) {
        var pos = transformation.getPosition();
        return viewMatrix.identity()
                .rotate(transformation.getRotation().invert(new org.joml.Quaternionf()))
                .translate(-pos.x, -pos.y, -pos.z);
    }
}
