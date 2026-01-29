package com.terminalvelocitycabbage.engine.util;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3i;

public class Transformation {


    Matrix4f transformationMatrix;

    Vector3f position;
    Quaternionf rotation;
    Vector3f scale;

    boolean dirty;

    public Transformation() {
        transformationMatrix = new Matrix4f();
        position = new Vector3f();
        rotation = new Quaternionf();
        scale = new Vector3f(1f);
        dirty = true;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Transformation translate(float x, float y, float z) {
        this.position.add(x, y, z);
        dirty = true;
        return this;
    }

    public Transformation setPosition(float x, float y, float z) {
        this.position.set(x, y, z);
        dirty = true;
        return this;
    }

    public Transformation setPosition(Vector3i position) {
        this.position.set(position.x, position.y, position.z);
        dirty = true;
        return this;
    }

    public Quaternionf getRotation() {
        return rotation;
    }

    public Transformation rotate(float x, float y, float z) {
        this.rotation.rotateYXZ((float) Math.toRadians(x), (float) Math.toRadians(y), (float) Math.toRadians(z));
        dirty = true;
        return this;
    }

    public Transformation setRotation(float x, float y, float z, float angle) {
        this.rotation.fromAxisAngleDeg(x, y, z, angle);
        dirty = true;
        return this;
    }

    public Vector3f getScale() {
        return scale;
    }

    public Transformation setScale(float scale) {
        this.scale.set(scale);
        dirty = true;
        return this;
    }

    public Transformation setScale(float x, float y, float z) {
        this.scale.set(x, y, z);
        dirty = true;
        return this;
    }

    public Transformation addScale(float x, float y, float z) {
        this.scale.add(x, y, z);
        dirty = true;
        return this;
    }

    public Transformation addScale(Vector3f scale) {
        this.scale.add(scale);
        dirty = true;
        return this;
    }

    public Matrix4f getTransformationMatrix() {
        if (dirty) updateTransformationMatrix();
        return transformationMatrix;
    }

    public void updateTransformationMatrix() {
        this.transformationMatrix.translationRotateScale(position, rotation, scale);
    }

    @Override
    public String toString() {
        return "Transformation{" +
                "position=" + position +
                ", rotation=" + rotation +
                ", scale=" + scale +
                '}';
    }
}
