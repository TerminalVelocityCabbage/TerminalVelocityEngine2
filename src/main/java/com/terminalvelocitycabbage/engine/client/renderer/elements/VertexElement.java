package com.terminalvelocitycabbage.engine.client.renderer.elements;

public class VertexElement {

    public static final VertexElement XYZ_POSITION = new VertexElement("position", 3, false);
    public static final VertexElement UV = new VertexElement("uv", 2, false);
    public static final VertexElement RGB_NORMAL = new VertexElement("normal", 3, true);
    public static final VertexElement RGBA_COLOR = new VertexElement("color", 4, false);

    private final String name;
    private final int components;
    private final boolean normalized;

    public VertexElement(String name, int components, boolean normalized) {
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
