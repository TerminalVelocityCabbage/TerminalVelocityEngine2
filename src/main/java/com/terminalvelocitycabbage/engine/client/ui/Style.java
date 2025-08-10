package com.terminalvelocitycabbage.engine.client.ui;

import com.terminalvelocitycabbage.engine.registry.Identifier;

public class Style {

    Identifier textureIdentifier;

    public Style(Identifier textureIdentifier) {
        this.textureIdentifier = textureIdentifier;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Identifier textureIdentifier;

        public Builder() {
            this.textureIdentifier = null;
        }

        public Builder setTexture(Identifier textureIdentifier) {
            this.textureIdentifier = textureIdentifier;
            return this;
        }

        public Style build() {
            return new Style(textureIdentifier);
        }
    }

    public Identifier getTextureIdentifier() {
        return textureIdentifier;
    }

    @Override
    public String toString() {
        return "Style{" +
                "textureIdentifier=" + textureIdentifier +
                '}';
    }
}
