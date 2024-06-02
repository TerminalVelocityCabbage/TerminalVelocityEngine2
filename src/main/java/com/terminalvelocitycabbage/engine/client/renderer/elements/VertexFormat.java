package com.terminalvelocitycabbage.engine.client.renderer.elements;

import java.util.HashMap;
import java.util.Map;

public class VertexFormat {

    private int stride;
    private int numComponents;

    public VertexFormat(Map<VertexElement, Integer> vertexElementOffsetMap) {
        vertexElementOffsetMap.forEach((vertexElement, offset) -> {
            stride += vertexElement.getNumComponents() * vertexElement.getComponentByteSize();
            numComponents += vertexElement.getNumComponents();
        });
    }

    public Builder builder() {
        return new Builder();
    }

    public int getStride() {
        return stride;
    }

    public int getNumComponents() {
        return numComponents;
    }

    static class Builder {

        private final Map<VertexElement, Integer> elementsOffsetMap;
        private int currentOffset;

        private Builder() {
            elementsOffsetMap = new HashMap<>();
            currentOffset = 0;
        }

        public Builder addElement(VertexElement element) {
            elementsOffsetMap.put(element, currentOffset);
            currentOffset += element.getNumComponents();
            return this;
        }

        public VertexFormat build() {
            return new VertexFormat(elementsOffsetMap);
        }

    }

}
