package com.terminalvelocitycabbage.engine.client.renderer.model;

import com.terminalvelocitycabbage.engine.client.renderer.elements.VertexFormat;

public class Meshes {

    public static Mesh createQuad(VertexFormat format) {
        Vertex[] vertices = new Vertex[] {
                new Vertex(format).setXYZPosition(-1, 1, 0).setUV(0, 1),
                new Vertex(format).setXYZPosition(-1, -1, 0).setUV(0, 0),
                new Vertex(format).setXYZPosition(1, -1, 0).setUV(1, 0),
                new Vertex(format).setXYZPosition(1, 1, 0).setUV(1, 1)
        };
        int[] indices = new int[] {
                0, 1, 2,
                2, 3, 0
        };
        return new Mesh(format, vertices, indices);
    }

}
