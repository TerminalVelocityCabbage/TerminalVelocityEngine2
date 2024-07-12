package com.terminalvelocitycabbage.engine.client.renderer.shader;

import com.terminalvelocitycabbage.engine.client.renderer.elements.VertexFormat;
import com.terminalvelocitycabbage.engine.registry.Identifier;

import java.util.*;

/**
 * Configures a shader program for ease of use by the user
 */
public class ShaderProgramConfig {

    VertexFormat vertexFormat;
    List<Shader> shaders;
    Map<String, Uniform> uniforms;

    private ShaderProgramConfig(VertexFormat vertexFormat, List<Shader> shaders, Map<String, Uniform> uniforms) {
        this.vertexFormat = vertexFormat;
        this.shaders = shaders;
        this.uniforms = uniforms;
    }

    /**
     * @return A builder to make configuring a config easy
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * @return The format of this config
     */
    public VertexFormat getVertexFormat() {
        return vertexFormat;
    }

    /**
     * @return The shaders used by this program config
     */
    public List<Shader> getShaders() {
        return shaders;
    }

    /**
     * @return The uniforms used by this program
     */
    public Collection<Uniform> getUniforms() {
        return uniforms.values();
    }

    /**
     * @param uniformName The name of the uniform to retrieve
     * @return The uniform belonging to this program by the specified name
     */
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

        /**
         * @param vertexFormat The vertex format that the vertex shader in this program will be using
         * @return
         */
        public Builder vertexFormat(VertexFormat vertexFormat) {
            this.vertexFormat = vertexFormat;
            return this;
        }

        /**
         * @param type the type of shader that these sources define
         * @param resourceIdentifier The resource identifier that this shader source cen be found from
         * @return this builder for easy chaining
         */
        public Builder addShader(Shader.Type type, Identifier resourceIdentifier) {
            shaders.add(new Shader(type, resourceIdentifier));
            return this;
        }

        /**
         * @param uniform A uniform to add to this program
         * @return this builder for easy chaining
         */
        public Builder addUniform(Uniform uniform) {
            uniforms.put(uniform.getName(), uniform);
            return this;
        }

        /**
         * @return The built shader program config as defined by the nodes of this builder
         */
        public ShaderProgramConfig build() {
            return new ShaderProgramConfig(vertexFormat, shaders, uniforms);
        }
    }
}
