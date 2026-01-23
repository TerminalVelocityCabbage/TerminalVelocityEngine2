package com.terminalvelocitycabbage.engine.client.ui.data;

public record AspectRatioElementConfig(float aspectRatio) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private float aspectRatio;

        public Builder aspectRatio(float aspectRatio) {
            this.aspectRatio = aspectRatio;
            return this;
        }

        public AspectRatioElementConfig build() {
            return new AspectRatioElementConfig(aspectRatio);
        }
    }
}
