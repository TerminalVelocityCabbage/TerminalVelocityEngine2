package com.terminalvelocitycabbage.engine.client.renderer.elements;

public enum VertexElement {

    XYZ_POSITION("position", 3, false),
    UV("uv", 2, false),
    RGB_NORMAL("normal", 3, true),
    RGBA_COLOR("color", 4, false);

    private final String name;
    private final int components;
    private final boolean normalized;

    VertexElement(String name, int components, boolean normalized) {
        this.name = name;
        this.components = components;
        this.normalized = normalized;
    }

    public String getName() {
        return name;
    }

    public int getNumComponents() {
        return components;
    }

    public int getComponentByteSize() {
        return Float.BYTES;
    }

    public boolean isNormalized() {
        return normalized;
    }
}
