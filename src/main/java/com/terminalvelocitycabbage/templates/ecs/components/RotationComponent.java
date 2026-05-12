package com.terminalvelocitycabbage.templates.ecs.components;

import com.terminalvelocitycabbage.editor.hints.EditorHint;
import com.terminalvelocitycabbage.engine.ecs.Component;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@EditorHint.ComponentName(name = "Rotation")
public class RotationComponent implements Component {

    Quaternionf rotation;

    @Override
    public void setDefaults() {
        rotation = new Quaternionf();
    }

    public Quaternionf getRotation() {
        return rotation;
    }

    public void setRotation(Quaternionf rotation) {
        this.rotation = rotation;
    }

    public void setRotation(float x, float y, float z) {
        this.rotation.identity().rotateXYZ(x, y, z);
    }

    public void rotate(Vector3f rotation) {
        this.rotation.rotateXYZ(rotation.x, rotation.y, rotation.z);
    }

    public void rotate(float x, float y, float z) {
        this.rotation.rotateXYZ(x, y, z);
    }

    public void rotateAxis(float angle, Vector3f axis) {
        this.rotation.rotateAxis(angle, axis);
    }
}
