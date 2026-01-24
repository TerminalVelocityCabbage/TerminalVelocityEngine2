package com.terminalvelocitycabbage.engine.client.ui.data;

public record Sizing(SizingAxis width, SizingAxis height) {

    public static Sizing grow() {
        return new Sizing(SizingAxis.grow(), SizingAxis.grow());
    }

    public static Sizing fit() {
        return new Sizing(SizingAxis.fit(), SizingAxis.fit());
    }

}
