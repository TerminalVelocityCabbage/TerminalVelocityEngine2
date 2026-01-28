package com.terminalvelocitycabbage.engine.client.ui.data;

import java.util.Objects;

public final class CornerRadius {

    private float topLeft;
    private float topRight;
    private float bottomLeft;
    private float bottomRight;

    public CornerRadius() {
        this(0);
    }

    public CornerRadius(float all) {
        this(all, all, all, all);
    }

    public CornerRadius(float topLeft, float topRight, float bottomLeft, float bottomRight) {
        this.topLeft = topLeft;
        this.topRight = topRight;
        this.bottomLeft = bottomLeft;
        this.bottomRight = bottomRight;
    }

    public CornerRadius all(float radius) {
        topLeft = radius;
        topRight = radius;
        bottomLeft = radius;
        bottomRight = radius;
        return this;
    }

    public CornerRadius topLeft(float topLeft) {
        this.topLeft = topLeft;
        return this;
    }

    public CornerRadius topRight(float topRight) {
        this.topRight = topRight;
        return this;
    }

    public CornerRadius bottomLeft(float bottomLeft) {
        this.bottomLeft = bottomLeft;
        return this;
    }

    public CornerRadius bottomRight(float bottomRight) {
        this.bottomRight = bottomRight;
        return this;
    }

    public float topLeft() {
        return topLeft;
    }

    public float topRight() {
        return topRight;
    }

    public float bottomLeft() {
        return bottomLeft;
    }

    public float bottomRight() {
        return bottomRight;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (CornerRadius) obj;
        return Float.floatToIntBits(this.topLeft) == Float.floatToIntBits(that.topLeft) &&
                Float.floatToIntBits(this.topRight) == Float.floatToIntBits(that.topRight) &&
                Float.floatToIntBits(this.bottomLeft) == Float.floatToIntBits(that.bottomLeft) &&
                Float.floatToIntBits(this.bottomRight) == Float.floatToIntBits(that.bottomRight);
    }

    @Override
    public int hashCode() {
        return Objects.hash(topLeft, topRight, bottomLeft, bottomRight);
    }

    @Override
    public String toString() {
        return "CornerRadius[" +
                "topLeft=" + topLeft + ", " +
                "topRight=" + topRight + ", " +
                "bottomLeft=" + bottomLeft + ", " +
                "bottomRight=" + bottomRight + ']';
    }
}
