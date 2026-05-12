package com.terminalvelocitycabbage.templates.ecs.components;

import com.terminalvelocitycabbage.editor.hints.EditorHint;
import com.terminalvelocitycabbage.engine.ecs.Component;
import org.joml.Vector2f;

//TODO change setter methods to have a min and max y rotation and to return a normalized x rotation
@EditorHint.ComponentName(name = "Pitch-Yaw Rotation")
public class PitchYawRotationComponent implements Component {

    Vector2f rotation;

    @Override
    public void setDefaults() {
        rotation = new Vector2f();
    }

    public Vector2f getRotation() {
        return rotation;
    }

    public void setRotation(Vector2f rotation) {
        this.rotation = rotation;
    }

    public void setRotation(float x, float y) {
        this.rotation.set(x, y);
    }

    public void rotate(float x, float y) {
        this.rotation.x += x;
        this.rotation.y += y;
    }
}
