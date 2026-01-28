package com.terminalvelocitycabbage.engine.client.ui.data.configs;

import com.terminalvelocitycabbage.engine.client.ui.data.CornerRadius;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.util.Color;

public record ImageElementConfig(Identifier imageIdentifier, Identifier atlasIdentifier, CornerRadius cornerRadius, Color backgroundColor) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Identifier imageIdentifier;
        private Identifier atlasIdentifier;
        private CornerRadius cornerRadius;
        private Color backgroundColor;

        public Builder imageIdentifier(Identifier imageIdentifier) {
            this.imageIdentifier = imageIdentifier;
            return this;
        }

        public Builder atlasIdentifier(Identifier atlasIdentifier) {
            this.atlasIdentifier = atlasIdentifier;
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
            return new ImageElementConfig(imageIdentifier, atlasIdentifier, cornerRadius, backgroundColor);
        }
    }
}
