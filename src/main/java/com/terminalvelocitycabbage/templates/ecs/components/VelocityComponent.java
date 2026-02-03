package com.terminalvelocitycabbage.templates.ecs.components;

import com.terminalvelocitycabbage.engine.ecs.Component;
import org.joml.Vector3f;

public class VelocityComponent implements Component {

    Vector3f velocity;

    @Override
    public void setDefaults() {
        velocity = new Vector3f(0, 0, 0);
    }

    @Override
    public void cleanup() {
        Component.super.cleanup();
    }

    public Vector3f getVelocity() {
        return velocity;
    }

    public VelocityComponent setVelocity(float x, float y, float z) {
        velocity.set(x, y, z);
        return this;
    }

    public VelocityComponent setVelocity(Vector3f velocity) {
        this.velocity.set(velocity);
        return this;
    }

    public VelocityComponent addVelocity(float x, float y, float z) {
        velocity.add(x, y, z);
        return this;
    }

    public VelocityComponent addVelocity(Vector3f velocity) {
        this.velocity.add(velocity);
        return this;
    }
}
