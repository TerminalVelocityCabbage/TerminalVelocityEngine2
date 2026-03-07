package com.terminalvelocitycabbage.templates.ecs.components;

import com.terminalvelocitycabbage.engine.ecs.Component;
import com.terminalvelocitycabbage.engine.util.Transformation;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3i;

public class TransformationComponent implements Component {

    Transformation transformation;

    @Override
    public void setDefaults() {
        this.transformation = new Transformation();
    }

    public Vector3f getPosition() {
        return transformation.getPosition();
    }

    public TransformationComponent setPosition(float x, float y, float z) {
        transformation.setPosition(x, y, z);
        return this;
    }

    public TransformationComponent setPosition(Vector3f position) {
        transformation.setPosition(position);
        return this;
    }

    public TransformationComponent translate(float x, float y, float z) {
        transformation.translate(x, y, z);
        return this;
    }

    public Quaternionf getRotation() {
        return transformation.getRotation();
    }

    public TransformationComponent rotate(float x, float y, float z) {
        transformation.rotate(x, y, z);
        return this;
    }

    public TransformationComponent setRotation(float x, float y, float z, float angle) {
        transformation.setRotation(x, y, z, angle);
        return this;
    }

    public Vector3f getScale() {
        return transformation.getScale();
    }

    public TransformationComponent setScale(float scale) {
        transformation.setScale(scale);
        return this;
    }

    public TransformationComponent setScale(float scaleX, float scaleY, float scaleZ) {
        transformation.setScale(scaleX, scaleY, scaleZ);
        return this;
    }

    public Transformation getTransformation() {
        return transformation;
    }

    public Matrix4f getTransformationMatrix() {
        return transformation.getTransformationMatrix();
    }
}
