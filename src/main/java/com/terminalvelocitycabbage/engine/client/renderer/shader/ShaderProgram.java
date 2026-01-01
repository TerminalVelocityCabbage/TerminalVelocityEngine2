package com.terminalvelocitycabbage.engine.client.renderer.shader;

import com.terminalvelocitycabbage.engine.debug.Log;

import static org.lwjgl.opengl.GL20.*;

public class ShaderProgram {

    private final int programID;
    ShaderProgramConfig config;

    private ShaderProgram(ShaderProgramConfig config) {

        this.config = config;

        Shader[] shaders = config.getShaders().toArray(new Shader[0]);

        //Create this shader program and make sure it was successful
        programID = glCreateProgram();
        if (programID == 0) Log.crash("Could not create shader program");

        //Create shaders and attach them to this program
        for (Shader shader : shaders) {
            var shaderID = shader.create();
            glAttachShader(programID, shaderID);
            glDeleteShader(shaderID);
        }

        //Link this program
        glLinkProgram(programID);
        if (glGetProgrami(programID, GL_LINK_STATUS) == GL_FALSE) Log.crash("Could not link shader program");

        //Create uniforms for this shader
        config.getUniforms().forEach(uniform -> uniform.create(programID));
    }

    /**
     * @param config A shader program config that will define a new ShaderProgram
     * @return a ShaderProgram configured by the config
     */
    public static ShaderProgram of(ShaderProgramConfig config) {
        return new ShaderProgram(config);
    }

    /**
     * Binds this shader program
     */
    public void bind() {
        glUseProgram(programID);
    }

    /**
     * @param uniformName the name of the uniform to get (as used in the shader)
     * @return An {@link Uniform} for your use
     */
    public Uniform getUniform(String uniformName) {
        return config.getUniform(uniformName);
    }

    /**
     * unbinds this shaderprogram
     */
    public void unbind() {
        glUseProgram(0);
    }

    /**
     * cleans up this shader program
     */
    public void cleanup() {
        unbind();
        if (programID != 0) glDeleteProgram(programID);
    }

    /**
     * @return The OpenGL identifier of this shader program
     */
    public int getProgramID() {
        return programID;
    }

    /**
     * @return a boolean to represent whether this shader is valid. Not always used because it's much slower, so use when needed
     */
    public boolean validate() {
        glValidateProgram(programID);
        return glGetProgrami(programID, GL_VALIDATE_STATUS) == GL_TRUE;
    }

    /**
     * @return The config that defines this shader
     */
    public ShaderProgramConfig getConfig() {
        return config;
    }
}
