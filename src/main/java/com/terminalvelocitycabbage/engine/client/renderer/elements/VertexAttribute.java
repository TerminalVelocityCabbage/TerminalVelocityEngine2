package com.terminalvelocitycabbage.engine.client.renderer.elements;

public enum VertexAttribute {

    XYZ_POSITION("position", "position", "vec3", 3, false),
    UV("uv", "textureCoord", "vec2", 2, false),
    XYZ_NORMAL("normal", "normal", "vec3", 3, true),
    RGBA_COLOR("color_rgba", "color", "vec4", 4, false),
    RGB_COLOR("color_rgb", "color", "vec3", 3, false),
    BONE_INDICES("bone_indices", "boneIndices", "vec4", 4, false);

    private final String name;
    private final int components;
    private final boolean normalized;
    private final String uniformName;
    private final String glslType;

    VertexAttribute(String name, String uniformName, String glslType, int components, boolean normalized) {
        this.name = name;
        this.components = components;
        this.normalized = normalized;
        this.uniformName = uniformName;
        this.glslType = glslType;
    }

    public String getName() {
        return name;
    }

    public int getNumComponents() {
        return components;
    }

    public int getComponentByteSize() {
        return Float.BYTES;
    }

    public boolean isNormalized() {
        return normalized;
    }

    public int getComponents() {
        return components;
    }

    public String getUniformName() {
        return uniformName;
    }

    public String getGlslType() {
        return glslType;
    }

    @Override
    public String toString() {
        return "VertexAttribute{" +
                "name='" + name + '\'' +
                ", components=" + components +
                ", normalized=" + normalized +
                ", uniformName='" + uniformName +
                ", glslType='" + glslType +
                '}';
    }
}
