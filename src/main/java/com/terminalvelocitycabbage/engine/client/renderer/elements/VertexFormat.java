package com.terminalvelocitycabbage.engine.client.renderer.elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VertexFormat {

    private int stride;
    private int numComponents;
    Map<VertexAttribute, Integer> vertexAttributeOffsetMap;
    List<VertexAttribute> attributes;

    public VertexFormat(List<VertexAttribute> attributes, Map<VertexAttribute, Integer> vertexElementOffsetMap) {
        this.vertexAttributeOffsetMap = vertexElementOffsetMap;
        vertexElementOffsetMap.forEach((element, offset) -> {
            stride += element.getNumComponents() * element.getComponentByteSize();
            numComponents += element.getNumComponents();
        });
        this.attributes = attributes;
    }

    public static Builder builder() {
        return new Builder();
    }

    public int getStride() {
        return stride;
    }

    public int getNumComponents() {
        return numComponents;
    }

    public int getOffset(VertexAttribute element) {
        return vertexAttributeOffsetMap.get(element);
    }

    public boolean containsElement(VertexAttribute vertexElement) {
        return vertexAttributeOffsetMap.containsKey(vertexElement);
    }

    public int getNumElements() {
        return vertexAttributeOffsetMap.size();
    }

    public VertexAttribute getElement(int elementIndex) {
        return attributes.get(elementIndex);
    }

    public List<VertexAttribute> getAttributes() {
        return attributes;
    }

    public boolean hasComponent(VertexAttribute vertexAttribute) {
        return attributes.contains(vertexAttribute);
    }

    public static class Builder {

        private final Map<VertexAttribute, Integer> elementsOffsetMap;
        private final List<VertexAttribute> attributes;
        private int currentOffset;

        private Builder() {
            elementsOffsetMap = new HashMap<>();
            attributes = new ArrayList<>();
            currentOffset = 0;
        }

        public Builder addElement(VertexAttribute element) {
            attributes.add(element);
            elementsOffsetMap.put(element, currentOffset);
            currentOffset += element.getNumComponents();
            return this;
        }

        public VertexFormat build() {
            return new VertexFormat(attributes, elementsOffsetMap);
        }

    }

}
