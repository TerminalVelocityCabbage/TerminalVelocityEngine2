package com.terminalvelocitycabbage.engine.client.renderer.shader;

import com.terminalvelocitycabbage.engine.debug.Log;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1i;

public class Uniform {

    String uniformName;
    int uniformLocation;
    int shaderProgramId;

    public Uniform(String uniformName) {
        this.uniformName = uniformName;
    }

    public void create(ShaderProgram shaderProgram) {
        shaderProgramId = shaderProgram.getProgramID();
        uniformLocation = glGetUniformLocation(shaderProgramId, uniformName);
        if (uniformLocation < 0) Log.crash("Could not find uniform " + uniformName + " in shader program: " + shaderProgramId);
    }

    //TODO add a lot more of these supported uniform types
    public void setUniform(int uniformValue) {
        glUniform1i(glGetUniformLocation(shaderProgramId, uniformName), uniformValue);
    }

    public String getName() {
        return uniformName;
    }
}
