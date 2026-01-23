package com.terminalvelocitycabbage.engine.client.ui.data;

import org.joml.Vector4f;

public record BorderElementConfig(Vector4f color, BorderWidth width) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Vector4f color;
        private BorderWidth width;

        public Builder color(Vector4f color) {
            this.color = color;
            return this;
        }

        public Builder width(BorderWidth width) {
            this.width = width;
            return this;
        }

        public BorderElementConfig build() {
            return new BorderElementConfig(color, width);
        }
    }
}
