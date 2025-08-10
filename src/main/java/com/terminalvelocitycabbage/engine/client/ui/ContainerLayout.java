package com.terminalvelocitycabbage.engine.client.ui;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class ContainerLayout extends Layout {

    Anchor anchor;
    PlacementDirection placementDirection;

    Matrix4f transformationMatrix;

    public ContainerLayout(Dimension width, Dimension height, Anchor anchor, PlacementDirection placementDirection) {
        super(width, height);
        this.anchor = anchor;
        this.placementDirection = placementDirection;
    }

    public ContainerLayout(Dimension width, Dimension height) {
        this(width, height, Anchor.CENTER_CENTER, PlacementDirection.CENTERED);
    }

    public ContainerLayout(int width, int height) {
        this(new Dimension(width, Unit.PIXELS), new Dimension(height, Unit.PIXELS));
    }

    @Override
    public void setDimensions(int width, int height) {
        super.setDimensions(width, height);
        this.computedWidth = width;
        this.computedHeight = height;
    }

    public enum Anchor {
        TOP_LEFT(1, -1),
        TOP_CENTER(1, 0),
        TOP_RIGHT(1, 1),
        CENTER_LEFT(0, -1),
        CENTER_CENTER(0, 0),
        CENTER_RIGHT(0, 1),
        BOTTOM_LEFT( -1, -1),
        BOTTOM_CENTER( -1, 0),
        BOTTOM_RIGHT( -1, 1),
        INHERIT(0, 0);

        public int verticalMultiplier;
        public int horizontalMultiplier;

        Anchor(int verticalMultiplier, int horizontalMultiplier) {
            this.verticalMultiplier = verticalMultiplier;
            this.horizontalMultiplier = horizontalMultiplier;
        }
    }

    public enum PlacementDirection {
        DOWN_RIGHT(-0.5f, 0.5f),
        DOWN(-0.5f, 0),
        DOWN_LEFT(-0.5f, -0.5f),
        RIGHT(0, 0.5f),
        CENTERED(0, 0),
        LEFT(0, -0.5f),
        UP_RIGHT(0.5f, 0.5f),
        UP(0.5f, 0),
        UP_LEFT(0.5f, -0.5f);

        float xMultiplier;
        float yMultiplier;

        PlacementDirection(float yOffset, float xOffset) {
            this.xMultiplier = xOffset;
            this.yMultiplier = yOffset;
        }
    }

    @Override
    public Matrix4f getTransformationMatrix(ContainerLayout currentContainerLayout, Layout previousElementLayout) {

        transformationMatrix = new Matrix4f();

        var pixelWidth = width.toPixelDimension(currentContainerLayout, true);
        var pixelHeight = height.toPixelDimension(currentContainerLayout, false);

        var containerPixelWidth = currentContainerLayout.getComputedWidth();
        var containerPixelHeight = currentContainerLayout.getComputedHeight();

        //Scale the object by its sizes
        transformationMatrix.scale(pixelWidth, pixelHeight, 1);
        //If the placement direction is not center, we want to move by half each dimension in the placement directions
        //This is so that once we scale it'll already be in the right place
        transformationMatrix.translateLocal(
                placementDirection.xMultiplier * pixelWidth,
                placementDirection.yMultiplier * pixelHeight,
                0);

        //Move the element to its proper location
        var parentTransformationMatrix = currentContainerLayout.getStoredTransformationMatrix();
        transformationMatrix.translateLocal(
                anchor.horizontalMultiplier * ((float) containerPixelWidth / 2),
                anchor.verticalMultiplier * ((float) containerPixelHeight / 2),
                0);
        //TODO precompute this somehow because it currently results in a one frame delay of proper layout generation
        if (parentTransformationMatrix != null) transformationMatrix.translateLocal(parentTransformationMatrix.getTranslation(new Vector3f()));
        return transformationMatrix;
    }

    public PlacementDirection getPlacementDirection() {
        return placementDirection;
    }

    public Anchor getAnchor() {
        return anchor;
    }

    public Matrix4f getStoredTransformationMatrix() {
        return transformationMatrix;
    }
}
