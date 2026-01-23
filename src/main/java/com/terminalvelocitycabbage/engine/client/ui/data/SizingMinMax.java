package com.terminalvelocitycabbage.engine.client.ui.data;

public record SizingMinMax(float min, float max) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private float min;
        private float max;

        public Builder min(float min) {
            this.min = min;
            return this;
        }

        public Builder max(float max) {
            this.max = max;
            return this;
        }

        public SizingMinMax build() {
            return new SizingMinMax(min, max);
        }
    }
}
