package com.terminalvelocitycabbage.engine.client.ui;

import com.terminalvelocitycabbage.engine.registry.Identifier;

public class Style {

    Identifier textureIdentifier;
    Identifier fontIdentifier;

    public Style(Identifier textureIdentifier, Identifier fontIdentifier) {
        this.textureIdentifier = textureIdentifier;
        this.fontIdentifier = fontIdentifier;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Identifier textureIdentifier;
        private Identifier fontIdentifier;

        public Builder() {
            this.textureIdentifier = null;
            this.fontIdentifier = null;
        }

        public Builder setTexture(Identifier textureIdentifier) {
            this.textureIdentifier = textureIdentifier;
            return this;
        }

        public Builder setFont(Identifier fontIdentifier) {
            this.fontIdentifier = fontIdentifier;
            return this;
        }

        public Style build() {
            return new Style(textureIdentifier, fontIdentifier);
        }
    }

    public Identifier getTextureIdentifier() {
        return textureIdentifier;
    }

    public Identifier getFontIdentifier() {
        return fontIdentifier;
    }

    @Override
    public String toString() {
        return "Style{" +
                "textureIdentifier=" + textureIdentifier +
                ", fontIdentifier=" + fontIdentifier +
                '}';
    }
}
