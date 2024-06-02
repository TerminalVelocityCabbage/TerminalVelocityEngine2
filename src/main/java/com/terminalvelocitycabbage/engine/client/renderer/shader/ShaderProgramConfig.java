package com.terminalvelocitycabbage.engine.client.renderer.shader;

import com.terminalvelocitycabbage.engine.registry.Identifier;

import java.util.ArrayList;
import java.util.List;

public class ShaderProgramConfig {

    List<Shader> shaders;

    private ShaderProgramConfig() {
        this.shaders = new ArrayList<>();
    }

    public static ShaderProgramConfig builder() {
        return new ShaderProgramConfig();
    }

    public ShaderProgramConfig addShader(Shader.Type type, Identifier resourceIdentifier) {
        shaders.add(new Shader(type, resourceIdentifier));
        return this;
    }

    public List<Shader> getShaders() {
        return shaders;
    }
}
