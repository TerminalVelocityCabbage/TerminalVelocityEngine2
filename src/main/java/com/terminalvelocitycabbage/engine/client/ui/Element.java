package com.terminalvelocitycabbage.engine.client.ui;

import com.terminalvelocitycabbage.engine.registry.Identifier;

public final class Element {

    private Identifier parent;
    private Layout layout;
    private Style style;

    public Element(Identifier parent, Layout layout, Style style) {
        this.parent = parent;
        this.layout = layout;
        this.style = style;
    }

    public void setParent(Identifier parent) {
        this.parent = parent;
    }

    public Identifier getParent() {
        return parent;
    }

    public Layout getLayout() {
        return layout;
    }

    public Style getStyle() {
        return style;
    }

    public void reset() {
        layout.reset();
    }

}
