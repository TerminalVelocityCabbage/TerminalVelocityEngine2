package com.terminalvelocitycabbage.engine.client.renderer.model;

import com.terminalvelocitycabbage.engine.client.renderer.elements.VertexAttribute;
import com.terminalvelocitycabbage.engine.client.renderer.elements.VertexFormat;

public class Meshes {

    public static Mesh createQuad(VertexFormat format) {
        Vertex[] vertices = new Vertex[] {
                new Vertex(format),
                new Vertex(format),
                new Vertex(format),
                new Vertex(format)
        };

        if (format.hasComponent(VertexAttribute.XYZ_POSITION)) {
            vertices[0].setXYZPosition(-1, 1, 0);
            vertices[1].setXYZPosition(-1, -1, 0);
            vertices[2].setXYZPosition(1, -1, 0);
            vertices[3].setXYZPosition(1, 1, 0);
        }

        if (format.hasComponent(VertexAttribute.UV)) {
            vertices[0].setUV(0, 1);
            vertices[1].setUV(0, 0);
            vertices[2].setUV(1, 0);
            vertices[3].setUV(1, 1);
        }
        int[] indices = new int[] {
                0, 1, 2,
                2, 3, 0
        };
        return new Mesh(format, vertices, indices);
    }

}
