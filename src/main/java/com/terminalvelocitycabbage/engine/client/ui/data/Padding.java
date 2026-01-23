package com.terminalvelocitycabbage.engine.client.ui.data;

public record Padding(int left, int right, int top, int bottom) {

    public static Padding all(int value) {
        return new Padding(value, value, value, value);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int left;
        private int right;
        private int top;
        private int bottom;

        public Builder left(int left) {
            this.left = left;
            return this;
        }

        public Builder right(int right) {
            this.right = right;
            return this;
        }

        public Builder top(int top) {
            this.top = top;
            return this;
        }

        public Builder bottom(int bottom) {
            this.bottom = bottom;
            return this;
        }

        public Padding build() {
            return new Padding(left, right, top, bottom);
        }
    }
}
