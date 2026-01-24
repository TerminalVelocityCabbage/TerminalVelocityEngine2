package com.terminalvelocitycabbage.engine.client.ui.data.configs;

import com.terminalvelocitycabbage.engine.client.ui.data.CornerRadius;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.util.Color;
import org.joml.Vector2f;

public record ImageElementConfig(Identifier imageIdentifier, Vector2f sourceDimensions, CornerRadius cornerRadius, Color backgroundColor) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Identifier imageIdentifier;
        private Vector2f sourceDimensions;
        private CornerRadius cornerRadius;
        private Color backgroundColor;

        public Builder imageIdentifier(Identifier imageIdentifier) {
            this.imageIdentifier = imageIdentifier;
            return this;
        }

        public Builder sourceDimensions(Vector2f sourceDimensions) {
            this.sourceDimensions = sourceDimensions;
            return this;
        }

        public Builder cornerRadius(CornerRadius cornerRadius) {
            this.cornerRadius = cornerRadius;
            return this;
        }

        public Builder backgroundColor(Color backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        public ImageElementConfig build() {
            return new ImageElementConfig(imageIdentifier, sourceDimensions, cornerRadius, backgroundColor);
        }
    }
}
