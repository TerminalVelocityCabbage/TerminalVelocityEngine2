package com.terminalvelocitycabbage.engine.client.renderer.shader;

import com.terminalvelocitycabbage.engine.client.renderer.elements.VertexFormat;
import com.terminalvelocitycabbage.engine.registry.Identifier;

import java.util.*;

public class ShaderProgramConfig {

    VertexFormat vertexFormat;
    List<Shader> shaders;
    Map<String, Uniform> uniforms;

    private ShaderProgramConfig(VertexFormat vertexFormat, List<Shader> shaders, Map<String, Uniform> uniforms) {
        this.vertexFormat = vertexFormat;
        this.shaders = shaders;
        this.uniforms = uniforms;
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

    public Collection<Uniform> getUniforms() {
        return uniforms.values();
    }

    public Uniform getUniform(String uniformName) {
        return uniforms.get(uniformName);
    }

    public static class Builder {
        VertexFormat vertexFormat;
        List<Shader> shaders;
        Map<String, Uniform> uniforms;

        private Builder() {
            shaders = new ArrayList<>();
            uniforms = new HashMap<>();
        }

        public Builder vertexFormat(VertexFormat vertexFormat) {
            this.vertexFormat = vertexFormat;
            return this;
        }

        public Builder addShader(Shader.Type type, Identifier resourceIdentifier) {
            shaders.add(new Shader(type, resourceIdentifier));
            return this;
        }

        public Builder addUniform(Uniform uniform) {
            uniforms.put(uniform.getName(), uniform);
            return this;
        }

        public ShaderProgramConfig build() {
            return new ShaderProgramConfig(vertexFormat, shaders, uniforms);
        }
    }
}
