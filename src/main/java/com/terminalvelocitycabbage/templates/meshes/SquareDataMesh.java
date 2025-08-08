package com.terminalvelocitycabbage.templates.meshes;

import com.terminalvelocitycabbage.engine.client.renderer.elements.VertexFormat;
import com.terminalvelocitycabbage.engine.client.renderer.model.DataMesh;
import com.terminalvelocitycabbage.engine.client.renderer.model.Vertex;

public class SquareDataMesh extends DataMesh {

    @Override
    public Vertex[] getVertices(VertexFormat format) {
        return new Vertex[] {
                new Vertex(format)
                        .setXYZPosition(-1f, 1f, 0f)
                        .setRGBColor(0.5f, 0.0f, 0.0f)
                        .setUV(0, 0),
                new Vertex(format)
                        .setXYZPosition(-1f, -1f, 0f)
                        .setRGBColor(0.0f, 0.5f, 0.0f)
                        .setUV(0, 1),
                new Vertex(format)
                        .setXYZPosition(1f, -1f, 0f)
                        .setRGBColor(0.0f, 0.0f, 0.5f)
                        .setUV(1, 1),
                new Vertex(format)
                        .setXYZPosition(1f, 1f, 0f)
                        .setRGBColor(0.0f, 0.5f, 0.5f)
                        .setUV(1, 0)
        };
    }

    @Override
    public int[] getIndices() {
        return new int[] {0, 1, 3, 3, 1, 2};
    }
}
