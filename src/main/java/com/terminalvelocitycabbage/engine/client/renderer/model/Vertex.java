package com.terminalvelocitycabbage.engine.client.renderer.model;

import com.terminalvelocitycabbage.engine.client.renderer.elements.VertexAttribute;
import com.terminalvelocitycabbage.engine.client.renderer.elements.VertexFormat;
import com.terminalvelocitycabbage.engine.debug.Log;

import java.util.Arrays;

public class Vertex {

    private final VertexFormat format;
    private final float[] data;

    public Vertex(VertexFormat format) {
        this.format = format;
        this.data = new float[format.getNumComponents()];
    }

    public Vertex setXYZPosition(float x, float y, float z) {
        var offset = getOffsetOrCrash(VertexAttribute.XYZ_POSITION);
        data[offset] = x;
        data[offset + 1] = y;
        data[offset + 2] = z;
        return this;
    }

    public Vertex setUV(float u, float v) {
        var offset = getOffsetOrCrash(VertexAttribute.UV);
        data[offset] = u;
        data[offset + 1] = v;
        return this;
    }

    public Vertex setXYZNormal(float x, float y, float z) {
        var offset = getOffsetOrCrash(VertexAttribute.XYZ_NORMAL);
        data[offset] = x;
        data[offset + 1] = y;
        data[offset + 2] = z;
        return this;
    }

    public Vertex setRGBColor(float r, float g, float b) {
        var offset = getOffsetOrCrash(VertexAttribute.RGB_COLOR);
        data[offset] = r;
        data[offset + 1] = g;
        data[offset + 2] = b;
        return this;
    }

    public Vertex setRGBAColor(float r, float g, float b, float a) {
        var offset = getOffsetOrCrash(VertexAttribute.RGBA_COLOR);
        data[offset] = r;
        data[offset + 1] = g;
        data[offset + 2] = b;
        data[offset + 3] = a;
        return this;
    }

    private int getOffsetOrCrash(VertexAttribute attribute) {
        if (!format.hasComponent(attribute)) Log.crash("The vertex format of this vertex does not include " + attribute.getName());
        return format.getOffset(attribute);
    }

    public float[] getSubData(VertexAttribute element) {
        var offset = format.getOffset(element);
        var end = offset + element.getNumComponents();
        return Arrays.copyOfRange(data, offset, end);
    }

    public int getNumComponents() {
        return format.getNumComponents();
    }

    public float[] getData(VertexFormat format) {
        return data;
    }
}
