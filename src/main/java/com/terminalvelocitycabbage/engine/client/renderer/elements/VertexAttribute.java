package com.terminalvelocitycabbage.engine.client.renderer.elements;

public enum VertexAttribute {

    XYZ_POSITION("position", 3, false),
    UV("uv", 2, false),
    RGB_NORMAL("normal", 3, true),
    RGBA_COLOR("color_rgba", 4, false),
    RGB_COLOR("color_rgb", 3, false);

    private final String name;
    private final int components;
    private final boolean normalized;

    VertexAttribute(String name, int components, boolean normalized) {
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
