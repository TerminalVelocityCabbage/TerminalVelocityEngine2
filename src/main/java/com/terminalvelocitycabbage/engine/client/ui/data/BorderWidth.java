package com.terminalvelocitycabbage.engine.client.ui.data;

public record BorderWidth(int left, int right, int top, int bottom, int betweenChildren) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int left;
        private int right;
        private int top;
        private int bottom;
        private int betweenChildren;

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

        public Builder betweenChildren(int betweenChildren) {
            this.betweenChildren = betweenChildren;
            return this;
        }

        public BorderWidth build() {
            return new BorderWidth(left, right, top, bottom, betweenChildren);
        }
    }
}
