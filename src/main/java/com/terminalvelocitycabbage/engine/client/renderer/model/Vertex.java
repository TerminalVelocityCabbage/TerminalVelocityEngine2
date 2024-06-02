package com.terminalvelocitycabbage.engine.client.renderer.model;

import com.terminalvelocitycabbage.engine.client.renderer.elements.VertexElement;
import com.terminalvelocitycabbage.engine.util.ArrayUtils;

import java.util.HashMap;
import java.util.Map;

public class Vertex {

    private final Map<VertexElement, float[]> data;
    private final int numComponents;

    private Vertex(int numComponents, Map<VertexElement, float[]> data) {
        this.numComponents = numComponents;
        this.data = data;
    }

    public static Builder builder() {
        return new Builder();
    }

    public float[] getSubData(VertexElement element) {
        return data.get(element);
    }

    public float[] getData() {
        return ArrayUtils.combineFloatArrays(data.values().stream().toList());
    }

    public static class Builder {

        int numComponents = 0;
        Map<VertexElement, float[]> data;

        private Builder() {
            this.data = new HashMap<>();
        }

        public Builder addElement(VertexElement element, float[] data) {
            this.data.put(element, data);
            numComponents += element.getNumComponents();
            return this;
        }

        public Vertex build() {
            return new Vertex(numComponents, data);
        }
    }
}
