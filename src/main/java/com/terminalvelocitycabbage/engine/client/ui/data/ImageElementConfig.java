package com.terminalvelocitycabbage.engine.client.ui.data;

import com.terminalvelocitycabbage.engine.registry.Identifier;
import org.joml.Vector4f;

public record ImageElementConfig(Identifier imageIdentifier, Dimensions sourceDimensions, CornerRadius cornerRadius, Vector4f backgroundColor) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Identifier imageIdentifier;
        private Dimensions sourceDimensions;
        private CornerRadius cornerRadius;
        private Vector4f backgroundColor;

        public Builder imageIdentifier(Identifier imageIdentifier) {
            this.imageIdentifier = imageIdentifier;
            return this;
        }

        public Builder sourceDimensions(Dimensions sourceDimensions) {
            this.sourceDimensions = sourceDimensions;
            return this;
        }

        public Builder cornerRadius(CornerRadius cornerRadius) {
            this.cornerRadius = cornerRadius;
            return this;
        }

        public Builder backgroundColor(Vector4f backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        public ImageElementConfig build() {
            return new ImageElementConfig(imageIdentifier, sourceDimensions, cornerRadius, backgroundColor);
        }
    }
}
