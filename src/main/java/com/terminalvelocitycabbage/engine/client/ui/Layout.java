package com.terminalvelocitycabbage.engine.client.ui;

import org.joml.Matrix4f;

public class Layout {

    Dimension width;
    Dimension height;
    Anchor anchor;
    PlacementDirection placementDirection;

    public Layout(Dimension width, Dimension height, Anchor anchor, PlacementDirection placementDirection) {
        this.width = width;
        this.height = height;
        this.anchor = anchor;
        this.placementDirection = placementDirection;
    }

    public Layout(Dimension width, Dimension height) {
        this(width, height, Anchor.INHERIT, PlacementDirection.CENTER_CENTER); //TODO down right
    }

    public Layout(int width, int height) {
        this(new Dimension(width, Unit.PIXELS), new Dimension(height, Unit.PIXELS));
    }

    public void setDimensions(int width, int height) {
        this.width = new Dimension(width, Unit.PIXELS);
        this.height = new Dimension(height, Unit.PIXELS);
    }

    public enum Anchor {
        TOP_LEFT,
        TOP_CENTER,
        TOP_RIGHT,
        CENTER_LEFT,
        CENTER_CENTER,
        CENTER_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_CENTER,
        BOTTOM_RIGHT,
        INHERIT
    }

    public enum PlacementDirection {
        DOWN_RIGHT,
        DOWN_CENTER,
        DOWN_LEFT,
        RIGHT_CENTER,
        CENTER_CENTER,
        LEFT_CENTER,
        UP_RIGHT,
        UP_CENTER,
        UP_LEFT;

        boolean transformedHorizontally() {
            return this != UP_CENTER && this != DOWN_CENTER && this != CENTER_CENTER;
        }

        boolean transformedVertically() {
            return this != LEFT_CENTER && this != RIGHT_CENTER && this != CENTER_CENTER;
        }
    }

    public enum Unit {
        PIXELS,
        PERCENTAGE;
    }

    public record Dimension(Integer value, Unit unit) {

        float toPixelDimension(Layout parentLayout, boolean width) {
            if (unit == Unit.PIXELS) return value;
            if (width) {
                return parentLayout.getWidth().value() * ((float) value / 100);
            } else {
                return parentLayout.getHeight().value() * ((float) value / 100);
            }
        }
    }

    public Matrix4f getTransformationMatrix(Layout currentContainerLayout) {

        Matrix4f transformationMatrix = new Matrix4f();

        //If the placement direction is not center, we want to move by half each dimension in the placement directions
        //This is so that once we scale it'll already be in the right place
        if (placementDirection.transformedHorizontally()) {
            transformationMatrix.translate((float) width.value / 2, 0, 0);
        }
        if (placementDirection.transformedVertically()) {
            transformationMatrix.translate(0, (float) height.value / 2, 0);
        }
        //Scale the object by its sizes
        transformationMatrix.scale(width.toPixelDimension(currentContainerLayout, true), height.toPixelDimension(currentContainerLayout, false), 1);
        //Move the element to its proper location
        return transformationMatrix;
    }

    public PlacementDirection getPlacementDirection() {
        return placementDirection;
    }

    public Anchor getAnchor() {
        return anchor;
    }

    public Dimension getHeight() {
        return height;
    }

    public Dimension getWidth() {
        return width;
    }
}
