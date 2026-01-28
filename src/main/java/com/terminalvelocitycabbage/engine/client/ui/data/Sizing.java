package com.terminalvelocitycabbage.engine.client.ui.data;

public record Sizing(SizingAxis width, SizingAxis height) {

    public SizingAxis main(boolean isHorizontal) {
        return isHorizontal ? width : height;
    }

    public SizingAxis cross(boolean isHorizontal) {
        return isHorizontal ? height : width;
    }

    public static Sizing grow() {
        return new Sizing(SizingAxis.grow(), SizingAxis.grow());
    }

    public static Sizing fit() {
        return new Sizing(SizingAxis.fit(), SizingAxis.fit());
    }

}
