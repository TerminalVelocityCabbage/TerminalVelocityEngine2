package com.terminalvelocitycabbage.engine.client.renderer.shader;

import com.terminalvelocitycabbage.engine.debug.Log;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL20.*;

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

    public void setUniform(Matrix4f matrix4f) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            glUniformMatrix4fv(glGetUniformLocation(shaderProgramId, uniformName), false, matrix4f.get(stack.mallocFloat(16)));
        }
    }

    public String getName() {
        return uniformName;
    }
}
