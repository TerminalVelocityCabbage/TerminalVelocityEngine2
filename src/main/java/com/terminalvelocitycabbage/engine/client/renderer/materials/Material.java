package com.terminalvelocitycabbage.engine.client.renderer.materials;

import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.util.Color;

public class Material {

    public static final Color DEFAULT_ALBEDO_COLOR = new Color(1.0f, 1.0f, 1.0f, 1.0f);

    //if the texture exists the colors below will be ignored by the shader.
    protected Texture texture;
    private final Color ambientColor;
    private final Color diffuseColor;
    private final Color specularColor;

    //One of these will be null
    private float reflectivity;
    private Texture reflectivityTexture;

    public static Builder builder() {
        return new Builder();
    }

    protected Material(Texture albedo, Color ambientColor, Color diffuseColor, Color specularColor, float reflectivity, Texture reflectivityTexture) {
        this.texture = albedo;
        this.ambientColor = ambientColor;
        this.diffuseColor = diffuseColor;
        this.specularColor = specularColor;
        this.reflectivity = reflectivity;
        this.reflectivityTexture = reflectivityTexture;
    }

    public static class Builder {

        //The normal color values of the texture
        private Texture albedoTexture;
        //A texture with only alpha channels determining reflectivity at a pixel
        private Texture reflectivityTexture;

        //If the albedo texture isn't set there needs to be some way of determining colors
        private Color ambientColor;
        private Color diffuseColor;
        private Color specularColor;
        //If a reflectivity texture isn't present
        private float reflectivity;

        public Builder texture(Texture texture) {
            this.albedoTexture = texture;
            return this;
        }

        public Builder reflectivity(Texture texture) {
            this.reflectivityTexture = texture;
            return this;
        }

        public Builder reflectivity(float reflectivity) {
            this.reflectivity = reflectivity;
            return this;
        }

        public Builder color(float r, float g, float b, float a) {
            this.ambientColor = new Color(r, g, b, a);
            return this;
        }

        public Builder ambientColor(float r, float g, float b, float a) {
            this.ambientColor = new Color(r, g, b, a);
            return this;
        }

        public Builder diffuseColor(float r, float g, float b, float a) {
            this.diffuseColor = new Color(r, g, b, a);
            return this;
        }

        public Builder specularColor(float r, float g, float b, float a) {
            this.specularColor = new Color(r, g, b, a);
            return this;
        }

        public Builder color(Color color) {
            this.ambientColor = color;
            return this;
        }

        public Builder ambientColor(Color color) {
            this.ambientColor = color;
            return this;
        }

        public Builder diffuseColor(Color color) {
            this.diffuseColor = color;
            return this;
        }

        public Builder specularColor(Color color) {
            this.specularColor = color;
            return this;
        }

        public Material build() {
            //Check colors
            if (ambientColor == null) ambientColor = DEFAULT_ALBEDO_COLOR;
            if (diffuseColor == null) diffuseColor = ambientColor;
            if (specularColor == null) specularColor = ambientColor;
            if (reflectivityTexture != null && albedoTexture != null && !(reflectivityTexture.getHeight() % albedoTexture.getWidth() == 0) && !(reflectivityTexture.width % albedoTexture.width == 0)) {
                Log.crash("Material Build error", new RuntimeException("The reflectivity texture must be the same size or a multiple of the albedo texture to be used."));
            }
            return new Material(albedoTexture, ambientColor, diffuseColor, specularColor, reflectivity, reflectivityTexture);
        }
    }

    public Texture getTexture() {
        return texture;
    }

    public Color getAmbientColor() {
        return ambientColor;
    }

    public Color getDiffuseColor() {
        return diffuseColor;
    }

    public Color getSpecularColor() {
        return specularColor;
    }

    public float getReflectivity() {
        return reflectivity;
    }

    public Texture getReflectivityTexture() {
        return reflectivityTexture;
    }

    public boolean hasTexture() {
        return texture != null;
    }

    public boolean hasReflectivityTexture() {
        return reflectivityTexture != null;
    }

    public void setColor(float r, float g, float b, float opacity) {
        this.ambientColor.set(r, g, b, opacity);
        this.specularColor.set(r, g, b, opacity);
        this.diffuseColor.set(r, g, b, opacity);
    }
}
