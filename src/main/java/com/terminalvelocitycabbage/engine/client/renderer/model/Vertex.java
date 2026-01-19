package com.terminalvelocitycabbage.engine.client.renderer.model;

import com.terminalvelocitycabbage.engine.client.renderer.elements.VertexAttribute;
import com.terminalvelocitycabbage.engine.client.renderer.elements.VertexFormat;
import org.joml.Vector3f;

import java.util.Arrays;

public class Vertex {

    private final VertexFormat format;
    private final float[] data;

    public Vertex(VertexFormat format) {
        this.format = format;
        this.data = new float[format.getNumComponents()];
    }

    public Vertex(Vertex vertex) {
        this.format = vertex.format;
        this.data = Arrays.copyOf(vertex.data, vertex.data.length);
    }

    /**
     * @return This vertex with the updated position data
     */
    public Vertex setXYZPosition(float x, float y, float z) {
        var offset = format.getOffset(VertexAttribute.XYZ_POSITION);
        data[offset] = x;
        data[offset + 1] = y;
        data[offset + 2] = z;
        return this;
    }

    /**
     * @return This vertex with the updated texture coordinate data
     */
    public Vertex setUV(float u, float v) {
        var offset = format.getOffset(VertexAttribute.UV);
        data[offset] = u;
        data[offset + 1] = v;
        return this;
    }

    /**
     * @return This vertex with the updated normal data
     */
    public Vertex setXYZNormal(float x, float y, float z) {
        var offset = format.getOffset(VertexAttribute.XYZ_NORMAL);
        data[offset] = x;
        data[offset + 1] = y;
        data[offset + 2] = z;
        return this;
    }

    /**
     * @return This vertex with the updated color data
     */
    public Vertex setRGBColor(float r, float g, float b) {
        var offset = format.getOffset(VertexAttribute.RGB_COLOR);
        data[offset] = r;
        data[offset + 1] = g;
        data[offset + 2] = b;
        return this;
    }

    /**
     * @return This vertex with the updated color data
     */
    public Vertex setRGBColor(Vector3f color) {
        var offset = format.getOffset(VertexAttribute.RGB_COLOR);
        data[offset] = color.x;
        data[offset + 1] = color.y;
        data[offset + 2] = color.z;
        return this;
    }

    /**
     * @return This vertex with the updated color data
     */
    public Vertex setRGBAColor(float r, float g, float b, float a) {
        var offset = format.getOffset(VertexAttribute.RGBA_COLOR);
        data[offset] = r;
        data[offset + 1] = g;
        data[offset + 2] = b;
        data[offset + 3] = a;
        return this;
    }

    /**
     * Gets sub-data from the vertex data by component
     * @param element The element for which the data is to be retrieved
     * @return a float array of data relating to the specified attribute
     */
    public float[] getSubData(VertexAttribute element) {
        var offset = format.getOffset(element);
        var end = offset + element.getNumComponents();
        return Arrays.copyOfRange(data, offset, end);
    }

    /**
     * @return This vertex's format
     */
    public VertexFormat getFormat() {
        return format;
    }

    /**
     * @return This vertex's data
     */
    public float[] getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Vertex{" +
                "format=" + format +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
