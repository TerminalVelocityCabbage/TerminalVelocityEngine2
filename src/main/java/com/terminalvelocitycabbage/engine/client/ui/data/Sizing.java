package com.terminalvelocitycabbage.engine.client.ui.data;

public record Sizing(SizingAxis width, SizingAxis height) {

    public static Sizing grow() {
        return new Sizing(SizingAxis.grow(), SizingAxis.grow());
    }

    public static Sizing fit() {
        return new Sizing(SizingAxis.fit(), SizingAxis.fit());
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private SizingAxis width;
        private SizingAxis height;

        public Builder width(SizingAxis width) {
            this.width = width;
            return this;
        }

        public Builder height(SizingAxis height) {
            this.height = height;
            return this;
        }

        public Sizing build() {
            return new Sizing(width, height);
        }
    }
}
