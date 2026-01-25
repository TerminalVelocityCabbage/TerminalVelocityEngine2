package com.terminalvelocitycabbage.engine.client.ui;

import com.terminalvelocitycabbage.engine.client.ui.data.BoundingBox;
import org.joml.Vector2f;

public class UIElement {

    private final int id;
    private final UIContext context;

    public UIElement(int id, UIContext context) {
        this.id = id;
        this.context = context;
    }

    public int id() {
        return id;
    }

    public BoundingBox boundingBox() {
        return context.getElementData(id).boundingBox();
    }

    public Vector2f preferredSize() {
        return context.getElementData(id).preferredSize();
    }

    public float preferredWidth() {
        return preferredSize().x;
    }

    public float preferredHeight() {
        return preferredSize().y;
    }

    public float x() {
        return boundingBox().position().x;
    }

    public float y() {
        return boundingBox().position().y;
    }

    public float width() {
        return boundingBox().size().x;
    }

    public float height() {
        return boundingBox().size().y;
    }
}
