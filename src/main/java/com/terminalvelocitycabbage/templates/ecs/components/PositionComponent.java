package com.terminalvelocitycabbage.templates.ecs.components;

import com.terminalvelocitycabbage.engine.ecs.Component;
import org.joml.Vector3f;

public class PositionComponent implements Component {

    Vector3f position;

    @Override
    public void setDefaults() {
        position = new Vector3f(0, 0, 0);
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void setPosition(float x, float y, float z) {
        this.position.set(x, y, z);
    }

    public Vector3f getPosition() {
        return position;
    }

    public void move(float x, float y, float z) {
        position.add(x, y, z);
    }

    public void move(Vector3f moveBy) {
        position.add(moveBy);
    }
}
