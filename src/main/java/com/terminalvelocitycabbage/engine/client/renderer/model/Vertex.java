package com.terminalvelocitycabbage.engine.client.renderer.model;

import com.terminalvelocitycabbage.engine.client.renderer.elements.VertexAttribute;
import com.terminalvelocitycabbage.engine.client.renderer.elements.VertexFormat;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.util.ArrayUtils;

import java.util.*;

public class Vertex {

    private final Map<VertexAttribute, float[]> data;
    private final int numComponents;

    private Vertex(int numComponents, Map<VertexAttribute, float[]> data) {
        this.numComponents = numComponents;
        this.data = data;
    }

    public static Builder builder() {
        return new Builder();
    }

    public float[] getSubData(VertexAttribute element) {
        return data.get(element);
    }

    public int getNumComponents() {
        return numComponents;
    }

    public float[] getData(VertexFormat format) {
        List<float[]> dataToCompile = new ArrayList<>();
        for (VertexAttribute attribute : format.getAttributes()) {
            dataToCompile.add(data.get(attribute));
        }
        return ArrayUtils.combineFloatArrays(dataToCompile);
    }

    public static class Builder {

        int numComponents = 0;
        private final Map<VertexAttribute, float[]> data;

        private Builder() {
            this.data = new HashMap<>();
        }

        public Builder addAttribute(VertexAttribute attribute, float[] data) {
            if (attribute.getNumComponents() != data.length) Log.crash("Number of components supplied in element data does not match expected number for attribute given: " + attribute.getName() + " " + Arrays.toString(data));
            this.data.put(attribute, data);
            numComponents += attribute.getNumComponents();
            return this;
        }

        public Vertex build() {
            return new Vertex(numComponents, data);
        }
    }
}
