package com.terminalvelocitycabbage.engine.client.renderer.elements;

import com.terminalvelocitycabbage.engine.debug.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents and ordered list of vertex attributes as used by a shader program.
 */
public class VertexFormat {

    private int stride; //The total number of bytes that this format needs
    private int numComponents; //The number of attributes this format contains added up
    Map<VertexAttribute, Integer> vertexAttributeOffsetMap; //A map for easy retrieval of the offset for each attribute
    List<VertexAttribute> attributes; //The list of actual attributes

    public VertexFormat(List<VertexAttribute> attributes, Map<VertexAttribute, Integer> vertexElementOffsetMap) {
        this.vertexAttributeOffsetMap = vertexElementOffsetMap;
        vertexElementOffsetMap.forEach((element, offset) -> {
            stride += element.getNumComponents() * element.getComponentByteSize();
            numComponents += element.getNumComponents();
        });
        this.attributes = attributes;
    }

    /**
     * @return A new {@link Builder} for easy use of building a Vertex Format
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * @return The total size in bytes that data with this format takes up
     */
    public int getStride() {
        return stride;
    }

    /**
     * @return The number of attributes that this format contains added up
     */
    public int getNumComponents() {
        return numComponents;
    }

    /**
     * @param attribute The {@link VertexAttribute} which you need the offset for
     * @return an int representing the offset of this attribute's data
     */
    public int getOffset(VertexAttribute attribute) {
        if (!hasComponent(attribute)) Log.crash("The vertex format of this vertex does not include " + attribute.getName());
        return vertexAttributeOffsetMap.get(attribute);
    }

    /**
     * @return A list of this format's attributes
     */
    public List<VertexAttribute> getAttributes() {
        return attributes;
    }

    /**
     * @param vertexAttribute The attribute you want to know if this format contains
     * @return whether this attribute is contained in this format
     */
    public boolean hasComponent(VertexAttribute vertexAttribute) {
        return attributes.contains(vertexAttribute);
    }

    /**
     * A required utility for building a vertex format, constructs all the strides and internal data for you
     */
    public static class Builder {

        private final Map<VertexAttribute, Integer> elementsOffsetMap;
        private final List<VertexAttribute> attributes;
        private int currentOffset;

        private Builder() {
            elementsOffsetMap = new HashMap<>();
            attributes = new ArrayList<>();
            currentOffset = 0;
        }

        /**
         * @param element the element you want to add to this format
         * @return This builder (for command chaining)
         */
        public Builder addElement(VertexAttribute element) {
            attributes.add(element);
            elementsOffsetMap.put(element, currentOffset);
            currentOffset += element.getNumComponents();
            return this;
        }

        /**
         * @return A resulting vertex format from this builder
         */
        public VertexFormat build() {
            return new VertexFormat(attributes, elementsOffsetMap);
        }

    }

}
