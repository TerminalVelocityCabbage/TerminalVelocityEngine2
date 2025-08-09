package com.terminalvelocitycabbage.engine.client.ui;

import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.util.Transformation;

public class Style {

    Identifier textureIdentifier;
    Transformation transformation;

    public Style(Identifier textureIdentifier, Transformation transformation) {
        this.textureIdentifier = textureIdentifier;
        this.transformation = transformation;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Identifier textureIdentifier;
        private final Transformation transformation;

        public Builder() {
            this.textureIdentifier = null;
            this.transformation = new Transformation();
        }

        public Builder setTexture(Identifier textureIdentifier) {
            this.textureIdentifier = textureIdentifier;
            return this;
        }

        public Builder setPosition(int x, int y) {
            transformation.setPosition(x, y, 0);
            return this;
        }

        public Builder setZIndex(int zIndex) {
            transformation.translate(0, 0, zIndex);
            return this;
        }

        public Builder setScale(float scale) {
            transformation.setScale(scale);
            return this;
        }

        public Style build() {
            this.transformation.translate(0, 0, -50); //zIndex makes more sense where a higher number is on top so we will make the default -50 and add to it
            return new Style(textureIdentifier, transformation);
        }
    }

    public Identifier getTextureIdentifier() {
        return textureIdentifier;
    }

    public Transformation getTransformation() {
        return transformation;
    }

    @Override
    public String toString() {
        return "Style{" +
                "textureIdentifier=" + textureIdentifier +
                ", transformation=" + transformation +
                '}';
    }
}
