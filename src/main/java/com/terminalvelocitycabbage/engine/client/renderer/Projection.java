package com.terminalvelocitycabbage.engine.client.renderer;

import org.joml.Matrix4f;

/**
 * A Utility class to represent a typical 3D projection
 */
public class Projection {

    private float fieldOfView;
    private float nearPlane;
    private float farPlane;

    private final Type type;
    private Matrix4f projectionMatrix;

    /**
     * @param type The {@link Projection.Type} of this projection
     * @param fieldOfView The field of view (in degrees, it will be converted to radians for you)
     * @param nearPlane The distance from the origin that defines the near plane
     * @param farPlane The distance from the origin that defines the far plane
     */
    public Projection(Type type, float fieldOfView, float nearPlane, float farPlane) {
        this.type = type;
        this.fieldOfView = (float) Math.toRadians(fieldOfView);
        this.nearPlane = nearPlane;
        this.farPlane = farPlane;
        projectionMatrix = new Matrix4f();
    }

    /**
     * @return The current projection matrix
     */
    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    /**
     * Updates the size of this projection matrix (usually) for when the size of the window changes
     *
     * @param width The width of (usually) the window
     * @param height The height of (usually) the window
     */
    public void updateProjectionMatrix(float width, float height) {
        switch (type) {
            case PERSPECTIVE -> projectionMatrix.setPerspective(fieldOfView, width / height, nearPlane, farPlane);
            case ORTHOGONAL -> projectionMatrix.setOrtho(0, width, 0, height, nearPlane, farPlane); //TODO verify
        }
    }

    /**
     * @param fieldOfView the field of view (in degrees) to update the matrix to use
     */
    public void setFieldOfView(float fieldOfView) {
        this.fieldOfView = (float) Math.toRadians(fieldOfView);
    }

    /**
     * @param nearPlane the distance from the origin that this projection should have the near plane located at
     */
    public void setNearPlane(float nearPlane) {
        this.nearPlane = nearPlane;
    }

    /**
     * @param farPlane the distance from the origin that this projection should have the far plane located at
     */
    public void setFarPlane(float farPlane) {
        this.farPlane = farPlane;
    }

    /**
     * An enum to define the supported types of projection that this Projection can be
     */
    public enum Type {
        PERSPECTIVE,
        ORTHOGONAL
    }

}
