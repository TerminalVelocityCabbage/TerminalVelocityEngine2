package com.terminalvelocitycabbage.engine.client.ui.data;

import java.util.Objects;

public final class Margin {

    private int left;
    private int right;
    private int top;
    private int bottom;

    public Margin() {
        this(0);
    }

    public Margin(int value) {
        this.left = value;
        this.right = value;
        this.top = value;
        this.bottom = value;
    }

    public Margin all(int value) {
        this.left = value;
        this.right = value;
        this.top = value;
        this.bottom = value;
        return this;
    }

    public Margin left(int left) {
        this.left = left;
        return this;
    }

    public Margin right(int right) {
        this.right = right;
        return this;
    }

    public Margin top(int top) {
        this.top = top;
        return this;
    }

    public Margin bottom(int bottom) {
        this.bottom = bottom;
        return this;
    }

    public int left() {
        return left;
    }

    public int right() {
        return right;
    }

    public int top() {
        return top;
    }

    public int bottom() {
        return bottom;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Margin) obj;
        return this.left == that.left &&
                this.right == that.right &&
                this.top == that.top &&
                this.bottom == that.bottom;
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right, top, bottom);
    }

    @Override
    public String toString() {
        return "Margin[" +
                "left=" + left + ", " +
                "right=" + right + ", " +
                "top=" + top + ", " +
                "bottom=" + bottom + ']';
    }

}
