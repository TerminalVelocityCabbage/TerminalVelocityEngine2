package com.terminalvelocitycabbage.templates.meshes;

import com.terminalvelocitycabbage.engine.client.renderer.elements.VertexAttribute;
import com.terminalvelocitycabbage.engine.client.renderer.elements.VertexFormat;
import com.terminalvelocitycabbage.engine.client.renderer.model.DataMesh;
import com.terminalvelocitycabbage.engine.client.renderer.model.Vertex;

public class SquareDataMesh extends DataMesh {

    //TODO add dimensions to a constructor unit square for empty constructor

    @Override
    public Vertex[] getVertices(VertexFormat format) {
        var vertices = new Vertex[] {
                new Vertex(format),
                new Vertex(format),
                new Vertex(format),
                new Vertex(format)
        };

        if (format.hasComponent(VertexAttribute.XYZ_POSITION)) {
            vertices[0].setXYZPosition(-0.5f, 0.5f, 0f);
            vertices[1].setXYZPosition(-0.5f, -0.5f, 0f);
            vertices[2].setXYZPosition(0.5f, -0.5f, 0f);
            vertices[3].setXYZPosition(0.5f, 0.5f, 0f);
        }

        if (format.hasComponent(VertexAttribute.UV)) {
            vertices[0].setUV(0, 0);
            vertices[1].setUV(0, 1);
            vertices[2].setUV(1, 1);
            vertices[3].setUV(1, 0);
        }

        if (format.hasComponent(VertexAttribute.RGB_COLOR)) {
            vertices[0].setRGBColor(0.5f, 0.0f, 0.0f);
            vertices[1].setRGBColor(0.0f, 0.5f, 0.0f);
            vertices[2].setRGBColor(0.0f, 0.0f, 0.5f);
            vertices[3].setRGBColor(0.0f, 0.5f, 0.5f);
        }

        if (format.hasComponent(VertexAttribute.XYZ_NORMAL)) {
            for (Vertex vertex : vertices) {
                vertex.setXYZNormal(0, 0, 1);
            }
        }

        return vertices;
    }

    @Override
    public int[] getIndices() {
        return new int[] {0, 1, 3, 3, 1, 2};
    }
}
