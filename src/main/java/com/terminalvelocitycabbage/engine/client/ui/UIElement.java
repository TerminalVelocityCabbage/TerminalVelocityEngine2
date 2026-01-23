package com.terminalvelocitycabbage.engine.client.ui;

import com.terminalvelocitycabbage.engine.client.ui.data.BoundingBox;

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

    public float x() {
        return boundingBox().x();
    }

    public float y() {
        return boundingBox().y();
    }

    public float width() {
        return boundingBox().width();
    }

    public float height() {
        return boundingBox().height();
    }
}
