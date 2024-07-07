package com.terminalvelocitycabbage.templates.ecs.components;

import com.terminalvelocitycabbage.engine.ecs.Component;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class TransformationComponent implements Component {

    Matrix4f transformationMatrix;

    Vector3f position;
    Quaternionf rotation;
    float scale;

    boolean dirty;

    @Override
    public void setDefaults() {
        transformationMatrix = new Matrix4f();
        position = new Vector3f();
        rotation = new Quaternionf();
        scale = 1f;
    }

    public Vector3f getPosition() {
        return position;
    }

    public TransformationComponent setPosition(float x, float y, float z) {
        this.position.set(x, y, z);
        dirty = true;
        return this;
    }

    public Quaternionf getRotation() {
        return rotation;
    }

    public TransformationComponent rotate(float x, float y, float z) {
        this.rotation.rotateYXZ((float) Math.toRadians(x), (float) Math.toRadians(y), (float) Math.toRadians(z));
        dirty = true;
        return this;
    }

    public TransformationComponent setRotation(float x, float y, float z, float angle) {
        this.rotation.fromAxisAngleDeg(x, y, z, angle);
        dirty = true;
        return this;
    }

    public float getScale() {
        return scale;
    }

    public TransformationComponent setScale(float scale) {
        this.scale = scale;
        dirty = true;
        return this;
    }

    public Matrix4f getTransformationMatrix() {
        return transformationMatrix;
    }

    public void updateTransformationMatrix() {
        this.transformationMatrix.translationRotateScale(position, rotation, scale);
    }

    public boolean isDirty() {
        return dirty;
    }
}
