package com.terminalvelocitycabbage.engine.client.renderer.shader;

import com.terminalvelocitycabbage.engine.client.renderer.lighting.DirectionalLight;
import com.terminalvelocitycabbage.engine.client.renderer.materials.Material;
import com.terminalvelocitycabbage.engine.util.Color;
import org.joml.*;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL20.*;

public class Uniform {

    String uniformName;
    int uniformLocation;
    int shaderProgramId;

    //TODO add type parameter for type validation later
    public Uniform(String uniformName) {
        this.uniformName = uniformName;
    }

    /**
     * Creates this uniform on the specified shader program
     *
     * @param shaderProgram The program that this uniform belongs to
     */
    protected void create(int shaderProgram) {
        shaderProgramId = shaderProgram;
        uniformLocation = glGetUniformLocation(shaderProgramId, uniformName); //This may result in 0 for uniforms with many components and location names
    }

    private void setUniform(int uniformLocation, int integerValue) {
        glUniform1i(uniformLocation, integerValue);
    }

    /**
     * @param integerValue the int value as defined by a java int to set this uniform value to
     */
    public void setUniform(int integerValue) {
        setUniform(uniformLocation, integerValue);
    }

    public void setUniform(int uniformLocation, float floatValue) {
        glUniform1f(uniformLocation, floatValue);
    }

    /**
     * @param floatValue the float value as defined by a java int to set this uniform value to
     */
    public void setUniform(float floatValue) {
        setUniform(uniformLocation, floatValue);
    }

    private void setUniform(int uniformLocation, Vector2f vector2f) {
        glUniform2f(uniformLocation, vector2f.x, vector2f.y);
    }

    public void setUniform(Vector2f vector2f) {
        setUniform(uniformLocation, vector2f);
    }

    private void setUniform(int uniformLocation, Vector3f vector3f) {
        glUniform3f(uniformLocation, vector3f.x, vector3f.y, vector3f.z);
    }

    public void setUniform(Vector3f vector3f) {
        setUniform(uniformLocation, vector3f);
    }

    private void setUniform(int uniformLocation, Vector4f vector4f) {
        glUniform4f(uniformLocation, vector4f.x, vector4f.y, vector4f.z, vector4f.w);
    }

    public void setUniform(Vector4f vector4f) {
        setUniform(uniformLocation, vector4f);
    }

    private void setUniform(int uniformLocation, Color color) {
        glUniform4f(uniformLocation, color.r(), color.g(), color.b(), color.a());
    }

    public void setUniform(Color color) {
        setUniform(uniformLocation, color);
    }

    public void setUniform(Matrix3f matrix3f) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            glUniformMatrix4fv(uniformLocation, false, matrix3f.get(stack.mallocFloat(9)));
        }
    }

    /**
     * @param matrix4f the mat4 value as defined by a Matrix4f to set this uniform value to
     */
    public void setUniform(Matrix4f matrix4f) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            glUniformMatrix4fv(uniformLocation, false, matrix4f.get(stack.mallocFloat(16)));
        }
    }

    public void setUniform(Matrix4f[] matrices) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            int length = matrices.length;
            float[] data = new float[length * 16];
            for (int i = 0; i < length; i++) {
                matrices[i].get(data, i * 16);
            }
            glUniformMatrix4fv(uniformLocation, false, data);
        }
    }

    private int getSubLocation(String subName) {
        return glGetUniformLocation(shaderProgramId, uniformName + subName);
    }

    public void setUniform(DirectionalLight directionalLight) {
        setUniform(getSubLocation(".direction"), directionalLight.getDirection());
        setUniform(getSubLocation(".color"), directionalLight.getColor());
        setUniform(getSubLocation(".intensity"), directionalLight.getIntensity());
    }

    public void setUniform(Material material) {
        setUniform(getSubLocation(".ambient"), material.getAmbientColor());
        setUniform(getSubLocation(".diffuse"), material.getDiffuseColor());
        setUniform(getSubLocation(".specular"), material.getSpecularColor());
        setUniform(getSubLocation(".hasTexturedReflectivity"), material.hasReflectivityTexture() ? 1 : 0);
        setUniform(getSubLocation(".hasTexture"), material.hasTexture() ? 1 : 0);
        setUniform(getSubLocation(".reflectivity"), material.getReflectivity());
    }

    //Should only be used by spot light setting point light unforms or by the actual point light setter that accounts for position
//    private void setUniform(String name, PointLight pointLight) {
//        setUniform(name + ".color", pointLight.getColor());
//        setUniform(name + ".position", pointLight.getPosition());
//        setUniform(name + ".intensity", pointLight.getIntensity());
//        Attenuation att = pointLight.getAttenuation();
//        setUniform(name + ".attenuation.constant", att.getConstant());
//        setUniform(name + ".attenuation.linear", att.getLinear());
//        setUniform(name + ".attenuation.exponential", att.getExponential());
//    }
//
//    public void setUniform(String name, PointLight pointLight, int position) {
//        setUniform(name + "[" + position + "]", pointLight);
//    }
//
//    public void setUniforms(String name, PointLight[] pointLights) {
//        for (int i = 0; i < (pointLights != null ? pointLights.length : 0); i++) {
//            setUniform(name, pointLights[i], i);
//        }
//    }
//
//    private void setUniform(String name, SpotLight spotLight) {
//        setUniform(name + ".color", spotLight.getColor());
//        setUniform(name + ".position", spotLight.getPosition());
//        setUniform(name + ".intensity", spotLight.getIntensity());
//        Attenuation att = spotLight.getAttenuation();
//        setUniform(name + ".attenuation.constant", att.getConstant());
//        setUniform(name + ".attenuation.linear", att.getLinear());
//        setUniform(name + ".attenuation.exponential", att.getExponential());
//        setUniform(name + ".coneDirection", spotLight.getConeDirection());
//        setUniform(name + ".cutoff", spotLight.getCutoff());
//    }
//
//    public void setUniform(String name, SpotLight spotLight, int position) {
//        setUniform(name + "[" + position + "]", spotLight);
//    }
//
//    public void setUniform(String name, SpotLight[] spotLights) {
//        for (int i = 0; i < (spotLights != null ? spotLights.length : 0); i++) {
//            setUniform(name, spotLights[i], i);
//        }
//    }

    /**
     * @return the name of this uniform
     */
    public String getName() {
        return uniformName;
    }
}
