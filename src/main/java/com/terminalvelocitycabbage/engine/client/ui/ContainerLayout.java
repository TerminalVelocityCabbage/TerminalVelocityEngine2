package com.terminalvelocitycabbage.engine.client.ui;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class ContainerLayout extends Layout {

    Anchor anchor;
    PlacementDirection placementDirection;
    JustifyChildren justifyChildren;

    Matrix4f transformationMatrix;

    public ContainerLayout(Dimension width, Dimension height, Anchor anchor, PlacementDirection placementDirection, JustifyChildren justifyChildren) {
        super(width, height);
        this.anchor = anchor;
        this.placementDirection = placementDirection;
        this.justifyChildren = justifyChildren;
    }

    public ContainerLayout(Dimension width, Dimension height) {
        this(width, height, Anchor.CENTER_CENTER, PlacementDirection.CENTERED, JustifyChildren.CENTER_CENTER);
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
        BOTTOM_RIGHT( -1, 1);

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

    public enum JustifyChildren {
        TOP_LEFT(0.5f, -0.5f, PlacementDirection.DOWN_RIGHT),
        TOP_CENTER(0.5f, 0, PlacementDirection.DOWN),
        TOP_RIGHT(0.5f, 0.5f, PlacementDirection.DOWN_LEFT),
        CENTER_LEFT(0, -0.5f, PlacementDirection.RIGHT),
        CENTER_CENTER(0, 0, PlacementDirection.CENTERED),
        CENTER_RIGHT(0, 0.5f, PlacementDirection.LEFT),
        BOTTOM_LEFT( -0.5f, -0.5f, PlacementDirection.UP_RIGHT),
        BOTTOM_CENTER( -0.5f, 0, PlacementDirection.UP),
        BOTTOM_RIGHT( -0.5f, 0.5f, PlacementDirection.UP_LEFT);

        public float verticalMultiplier;
        public float horizontalMultiplier;
        public PlacementDirection placementDirection;

        JustifyChildren(float verticalMultiplier, float horizontalMultiplier, PlacementDirection placementDirection) {
            this.verticalMultiplier = verticalMultiplier;
            this.horizontalMultiplier = horizontalMultiplier;
            this.placementDirection = placementDirection;
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

    public JustifyChildren getChildJustification() {
        return justifyChildren;
    }

    public Matrix4f getStoredTransformationMatrix() {
        return transformationMatrix;
    }
}
