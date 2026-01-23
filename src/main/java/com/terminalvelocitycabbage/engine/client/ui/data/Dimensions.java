package com.terminalvelocitycabbage.engine.client.ui.data;

public record Dimensions(float width, float height) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private float width;
        private float height;

        public Builder width(float width) {
            this.width = width;
            return this;
        }

        public Builder height(float height) {
            this.height = height;
            return this;
        }

        public Dimensions build() {
            return new Dimensions(width, height);
        }
    }
}
