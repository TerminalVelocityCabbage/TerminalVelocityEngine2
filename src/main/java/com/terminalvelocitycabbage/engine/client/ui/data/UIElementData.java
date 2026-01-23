package com.terminalvelocitycabbage.engine.client.ui.data;

public record UIElementData(BoundingBox boundingBox, boolean found) {
    public static final UIElementData DEFAULT = new UIElementData(new BoundingBox(0, 0, 0, 0), false);
}
