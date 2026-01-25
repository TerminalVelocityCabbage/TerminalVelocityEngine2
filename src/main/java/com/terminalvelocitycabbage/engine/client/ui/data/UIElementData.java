package com.terminalvelocitycabbage.engine.client.ui.data;

import org.joml.Vector2f;

public record UIElementData(BoundingBox boundingBox, Vector2f preferredSize, boolean found) {
    public static final UIElementData DEFAULT = new UIElementData(new BoundingBox(new Vector2f(), new Vector2f()), new Vector2f(), false);
}
