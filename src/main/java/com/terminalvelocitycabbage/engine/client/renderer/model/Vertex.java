package com.terminalvelocitycabbage.engine.client.renderer.model;

import com.terminalvelocitycabbage.engine.client.renderer.elements.VertexFormat;

public class Vertex {

    private final VertexFormat format;
    private final float[] data;

    public Vertex(VertexFormat format, float[] data) {
        this.format = format;
        this.data = data;
    }
}
