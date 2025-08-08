package com.terminalvelocitycabbage.engine.client.renderer.model;

import com.terminalvelocitycabbage.engine.client.renderer.elements.VertexFormat;

public abstract class DataMesh {

    public abstract Vertex[] getVertices(VertexFormat format);
    public abstract int[] getIndices();

}
