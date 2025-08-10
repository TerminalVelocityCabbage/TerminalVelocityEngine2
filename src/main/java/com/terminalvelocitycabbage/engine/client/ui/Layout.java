package com.terminalvelocitycabbage.engine.client.ui;

import org.joml.Matrix4f;

public class Layout {

    Dimension width;
    Dimension height;

    //In Pixels
    int computedWidth;
    int computedHeight;

    public Layout(Dimension width, Dimension height) {
        this.width = width;
        this.height = height;
    }

    public Layout(int width, int height) {
        this(new Dimension(width, Unit.PIXELS), new Dimension(height, Unit.PIXELS));
    }

    public void computeDimensions(Layout parentLayout) {
        computedWidth = (int) (width.toPixelDimension(parentLayout, true));
        computedHeight = (int) (height.toPixelDimension(parentLayout, false));
    }

    public void setDimensions(int width, int height) {
        this.width = new Dimension(width, Unit.PIXELS);
        this.height = new Dimension(height, Unit.PIXELS);
    }

    public enum Unit {
        PIXELS,
        PERCENT;
    }

    public record Dimension(Integer value, Unit unit) {

        float toPixelDimension(Layout parentLayout, boolean width) {
            if (unit == Unit.PIXELS) return value;
            if (width) {
                return parentLayout.getComputedWidth() * ((float) value / 100);
            } else {
                return parentLayout.getComputedHeight() * ((float) value / 100);
            }
        }

        @Override
        public String toString() {
            return "Dimension{" +
                    "value=" + value +
                    ", unit=" + unit +
                    '}';
        }
    }

    public Matrix4f getTransformationMatrix(Layout currentContainerLayout) {

        Matrix4f transformationMatrix = new Matrix4f();

        var pixelWidth = width.toPixelDimension(currentContainerLayout, true);
        var pixelHeight = height.toPixelDimension(currentContainerLayout, false);

        //Scale the object by its sizes
        transformationMatrix.scale(pixelWidth, pixelHeight, 1);

        return transformationMatrix;
    }

    public Dimension getHeight() {
        return height;
    }

    public Dimension getWidth() {
        return width;
    }

    public int getComputedWidth() {
        return computedWidth;
    }

    public int getComputedHeight() {
        return computedHeight;
    }
}
