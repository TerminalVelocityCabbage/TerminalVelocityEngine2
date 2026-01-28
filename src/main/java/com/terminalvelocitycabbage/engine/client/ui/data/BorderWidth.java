package com.terminalvelocitycabbage.engine.client.ui.data;

import java.util.Objects;

public final class BorderWidth {

    private int left;
    private int right;
    private int top;
    private int bottom;
    private int betweenChildren;

    public BorderWidth() {
        this(0);
    }

    public BorderWidth(int all) {
        this(all, all);
    }

    public BorderWidth(int border, int betweenChildren) {
        this(border, border, border, border, betweenChildren);
    }

    public BorderWidth(int left, int right, int top, int bottom, int betweenChildren) {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
        this.betweenChildren = betweenChildren;
    }

    public BorderWidth all(int dimension) {
        left = dimension;
        right = dimension;
        top = dimension;
        bottom = dimension;
        betweenChildren = dimension;
        return this;
    }

    public BorderWidth left(int left) {
        this.left = left;
        return this;
    }

    public BorderWidth right(int right) {
        this.right = right;
        return this;
    }

    public BorderWidth top(int top) {
        this.top = top;
        return this;
    }

    public BorderWidth bottom(int bottom) {
        this.bottom = bottom;
        return this;
    }

    public BorderWidth betweenChildren(int betweenChildren) {
        this.betweenChildren = betweenChildren;
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

    public int betweenChildren() {
        return betweenChildren;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (BorderWidth) obj;
        return this.left == that.left &&
                this.right == that.right &&
                this.top == that.top &&
                this.bottom == that.bottom &&
                this.betweenChildren == that.betweenChildren;
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right, top, bottom, betweenChildren);
    }

    @Override
    public String toString() {
        return "BorderWidth[" +
                "left=" + left + ", " +
                "right=" + right + ", " +
                "top=" + top + ", " +
                "bottom=" + bottom + ", " +
                "betweenChildren=" + betweenChildren + ']';
    }
}
