package com.terminalvelocitycabbage.engine.client.renderer.shader;

import com.terminalvelocitycabbage.engine.debug.Log;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

public class Uniform {

    String uniformName;
    int uniformLocation;
    int shaderProgramId;

    public Uniform(String uniformName) {
        this.uniformName = uniformName;
    }

    /**
     * Creates this uniform on the specified shader program
     *
     * @param shaderProgram The program that this uniform belongs to
     */
    public void create(int shaderProgram) {
        shaderProgramId = shaderProgram;
        uniformLocation = glGetUniformLocation(shaderProgramId, uniformName);
        if (uniformLocation < 0) Log.crash("Could not find uniform " + uniformName + " in shader program: " + shaderProgramId);
    }

    /**
     * @param uniformValue the int value as defined by a java int to set this uniform value to
     */
    //TODO add a lot more of these supported uniform types
    public void setUniform(int uniformValue) {
        glUniform1i(uniformLocation, uniformValue);
    }

    /**
     * @param matrix4f the mat4 value as defined by a Matrix4f to set this uniform value to
     */
    public void setUniform(Matrix4f matrix4f) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            glUniformMatrix4fv(uniformLocation, false, matrix4f.get(stack.mallocFloat(16)));
        }
    }

    public void setUniforms(Map<Integer, Matrix4f> boneTransformations) {
        //TODO using memory stack here may result in an overflow of stack space, might need to change to not use this
        try (MemoryStack stack = MemoryStack.stackPush()) {
            int length = boneTransformations != null ? boneTransformations.size() : 0;
            FloatBuffer fb = stack.mallocFloat(16 * length);
            for (int i = 0; i < length; i++) {
                boneTransformations.get(i).get(16 * i, fb);
            }
            glUniformMatrix4fv(uniformLocation, false, fb);
        }
    }

    /**
     * @return the name of this uniform
     */
    public String getName() {
        return uniformName;
    }
}
