package com.terminalvelocitycabbage.templates.meshes;

import com.terminalvelocitycabbage.engine.client.renderer.elements.VertexFormat;
import com.terminalvelocitycabbage.engine.client.renderer.model.DataMesh;
import com.terminalvelocitycabbage.engine.client.renderer.model.Vertex;

public class SquareDataMesh extends DataMesh {

    //TODO add dimensions to a constructor unit square for empty constructor

    @Override
    public Vertex[] getVertices(VertexFormat format) {
        return new Vertex[] {
                new Vertex(format)
                        .setXYZPosition(-0.5f, 0.5f, 0f)
                        .setUV(0, 0)
                        .setXYZNormal(0, 0, 1),
                new Vertex(format)
                        .setXYZPosition(-0.5f, -0.5f, 0f)
                        .setUV(0, 1)
                        .setXYZNormal(0, 0, 1),
                new Vertex(format)
                        .setXYZPosition(0.5f, -0.5f, 0f)
                        .setUV(1, 1)
                        .setXYZNormal(0, 0, 1),
                new Vertex(format)
                        .setXYZPosition(0.5f, 0.5f, 0f)
                        .setUV(1, 0)
                        .setXYZNormal(0, 0, 1)
        };
    }

    @Override
    public int[] getIndices() {
        return new int[] {0, 1, 3, 3, 1, 2};
    }
}
