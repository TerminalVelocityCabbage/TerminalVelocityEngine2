package com.terminalvelocitycabbage.templates.meshes;

import com.terminalvelocitycabbage.engine.client.renderer.elements.VertexAttribute;
import com.terminalvelocitycabbage.engine.client.renderer.elements.VertexFormat;
import com.terminalvelocitycabbage.engine.client.renderer.model.DataMesh;
import com.terminalvelocitycabbage.engine.client.renderer.model.Vertex;

public class SquareDataMesh extends DataMesh {

    //TODO add dimensions to a constructor unit square for empty constructor

    @Override
    public Vertex[] getVertices(VertexFormat format) {
        if (format.hasComponent(VertexAttribute.RGB_COLOR)) {
            return new Vertex[] {
                    new Vertex(format)
                            .setXYZPosition(-0.5f, 0.5f, 0f)
                            .setRGBColor(0.5f, 0.0f, 0.0f)
                            .setUV(0, 0),
                    new Vertex(format)
                            .setXYZPosition(-0.5f, -0.5f, 0f)
                            .setRGBColor(0.0f, 0.5f, 0.0f)
                            .setUV(0, 1),
                    new Vertex(format)
                            .setXYZPosition(0.5f, -0.5f, 0f)
                            .setRGBColor(0.0f, 0.0f, 0.5f)
                            .setUV(1, 1),
                    new Vertex(format)
                            .setXYZPosition(0.5f, 0.5f, 0f)
                            .setRGBColor(0.0f, 0.5f, 0.5f)
                            .setUV(1, 0)
            };
        } else {
            return new Vertex[] {
                    new Vertex(format)
                            .setXYZPosition(-0.5f, 0.5f, 0f)
                            .setUV(0, 0),
                    new Vertex(format)
                            .setXYZPosition(-0.5f, -0.5f, 0f)
                            .setUV(0, 1),
                    new Vertex(format)
                            .setXYZPosition(0.5f, -0.5f, 0f)
                            .setUV(1, 1),
                    new Vertex(format)
                            .setXYZPosition(0.5f, 0.5f, 0f)
                            .setUV(1, 0)
            };
        }
    }

    @Override
    public int[] getIndices() {
        return new int[] {0, 1, 3, 3, 1, 2};
    }
}
