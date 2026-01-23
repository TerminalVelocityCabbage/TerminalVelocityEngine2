package com.terminalvelocitycabbage.engine.client.ui.data;

public record CornerRadius(float topLeft, float topRight, float bottomLeft, float bottomRight) {

    public static CornerRadius all(float radius) {
        return new CornerRadius(radius, radius, radius, radius);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private float topLeft;
        private float topRight;
        private float bottomLeft;
        private float bottomRight;

        public Builder topLeft(float topLeft) {
            this.topLeft = topLeft;
            return this;
        }

        public Builder topRight(float topRight) {
            this.topRight = topRight;
            return this;
        }

        public Builder bottomLeft(float bottomLeft) {
            this.bottomLeft = bottomLeft;
            return this;
        }

        public Builder bottomRight(float bottomRight) {
            this.bottomRight = bottomRight;
            return this;
        }

        public CornerRadius build() {
            return new CornerRadius(topLeft, topRight, bottomLeft, bottomRight);
        }
    }
}
