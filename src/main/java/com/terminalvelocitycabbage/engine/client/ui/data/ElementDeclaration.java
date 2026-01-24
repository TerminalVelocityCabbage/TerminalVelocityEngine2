package com.terminalvelocitycabbage.engine.client.ui.data;

import com.terminalvelocitycabbage.engine.client.ui.data.configs.*;
import com.terminalvelocitycabbage.engine.util.Color;

public record ElementDeclaration(
        LayoutConfig layout,
        Color backgroundColor,
        CornerRadius cornerRadius,
        AspectRatioElementConfig aspectRatio,
        ImageElementConfig image,
        FloatingElementConfig floating,
        ClipElementConfig clip,
        BorderElementConfig border
) {

    //TODO: add a way to create an element declaration from a string with tailwind like classes

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private LayoutConfig layout;
        private Color backgroundColor;
        private CornerRadius cornerRadius;
        private AspectRatioElementConfig aspectRatio;
        private ImageElementConfig image;
        private FloatingElementConfig floating;
        private ClipElementConfig clip;
        private BorderElementConfig border;

        public Builder layout(LayoutConfig layout) {
            this.layout = layout;
            return this;
        }

        public Builder backgroundColor(Color backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        public Builder cornerRadius(CornerRadius cornerRadius) {
            this.cornerRadius = cornerRadius;
            return this;
        }

        public Builder aspectRatio(AspectRatioElementConfig aspectRatio) {
            this.aspectRatio = aspectRatio;
            return this;
        }

        public Builder image(ImageElementConfig image) {
            this.image = image;
            return this;
        }

        public Builder floating(FloatingElementConfig floating) {
            this.floating = floating;
            return this;
        }

        public Builder clip(ClipElementConfig clip) {
            this.clip = clip;
            return this;
        }

        public Builder border(BorderElementConfig border) {
            this.border = border;
            return this;
        }

        public ElementDeclaration build() {
            return new ElementDeclaration(layout, backgroundColor, cornerRadius, aspectRatio, image, floating, clip, border);
        }
    }
}
