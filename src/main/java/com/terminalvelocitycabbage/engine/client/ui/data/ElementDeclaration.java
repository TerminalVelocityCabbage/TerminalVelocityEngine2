package com.terminalvelocitycabbage.engine.client.ui.data;

import org.joml.Vector4f;

public record ElementDeclaration(
        LayoutConfig layout,
        Vector4f backgroundColor,
        CornerRadius cornerRadius,
        AspectRatioElementConfig aspectRatio,
        ImageElementConfig image,
        FloatingElementConfig floating,
        CustomElementConfig custom,
        ClipElementConfig clip,
        BorderElementConfig border,
        Object userData
) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private LayoutConfig layout;
        private Vector4f backgroundColor;
        private CornerRadius cornerRadius;
        private AspectRatioElementConfig aspectRatio;
        private ImageElementConfig image;
        private FloatingElementConfig floating;
        private CustomElementConfig custom;
        private ClipElementConfig clip;
        private BorderElementConfig border;
        private Object userData;

        public Builder layout(LayoutConfig layout) {
            this.layout = layout;
            return this;
        }

        public Builder backgroundColor(Vector4f backgroundColor) {
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

        public Builder custom(CustomElementConfig custom) {
            this.custom = custom;
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

        public Builder userData(Object userData) {
            this.userData = userData;
            return this;
        }

        public ElementDeclaration build() {
            return new ElementDeclaration(layout, backgroundColor, cornerRadius, aspectRatio, image, floating, custom, clip, border, userData);
        }
    }
}
