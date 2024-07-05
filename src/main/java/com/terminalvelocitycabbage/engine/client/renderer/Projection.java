package com.terminalvelocitycabbage.engine.client.renderer;

import org.joml.Matrix4f;

public class Projection {

    private float fieldOfView;
    private float nearPlane;
    private float farPlane;

    private final Type type;
    private Matrix4f projectionMatrix;

    public Projection(Type type, float fieldOfView, float nearPlane, float farPlane) {
        this.type = type;
        this.fieldOfView = (float) Math.toRadians(fieldOfView);
        this.nearPlane = nearPlane;
        this.farPlane = farPlane;
        projectionMatrix = new Matrix4f();
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public void updateProjectionMatrix(float width, float height) {
        switch (type) {
            case PERSPECTIVE -> projectionMatrix.setPerspective(fieldOfView, width / height, nearPlane, farPlane);
            case ORTHOGONAL -> projectionMatrix.setOrtho(0, width, 0, height, nearPlane, farPlane); //TODO verify
        }
    }

    public void setFieldOfView(float fieldOfView) {
        this.fieldOfView = fieldOfView;
    }

    public void setNearPlane(float nearPlane) {
        this.nearPlane = nearPlane;
    }

    public void setFarPlane(float farPlane) {
        this.farPlane = farPlane;
    }

    public enum Type {
        PERSPECTIVE,
        ORTHOGONAL
    }

}
