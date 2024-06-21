package com.terminalvelocitycabbage.engine.client.renderer.shader;

import com.terminalvelocitycabbage.engine.client.renderer.elements.VertexFormat;
import com.terminalvelocitycabbage.engine.registry.Identifier;

import java.util.ArrayList;
import java.util.List;

public class ShaderProgramConfig {

    VertexFormat vertexFormat;
    List<Shader> shaders;

    private ShaderProgramConfig(VertexFormat vertexFormat, List<Shader> shaders) {
        this.vertexFormat = vertexFormat;
        this.shaders = shaders;
    }

    public static Builder builder() {
        return new Builder();
    }

    public VertexFormat getVertexFormat() {
        return vertexFormat;
    }

    public List<Shader> getShaders() {
        return shaders;
    }

    public static class Builder {
        VertexFormat vertexFormat;
        List<Shader> shaders;

        private Builder() {
            shaders = new ArrayList<>();
        }

        public Builder vertexFormat(VertexFormat vertexFormat) {
            this.vertexFormat = vertexFormat;
            return this;
        }

        public Builder addShader(Shader.Type type, Identifier resourceIdentifier) {
            shaders.add(new Shader(type, resourceIdentifier));
            return this;
        }

        public ShaderProgramConfig build() {
            return new ShaderProgramConfig(vertexFormat, shaders);
        }
    }
}
