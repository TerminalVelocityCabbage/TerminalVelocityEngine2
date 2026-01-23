package com.terminalvelocitycabbage.engine.client.ui.data;

import com.terminalvelocitycabbage.engine.client.ui.SizingType;

public record SizingAxis(SizingMinMax minMax, float percent, SizingType type) {

    public static SizingAxis fit(float min, float max) {
        return new SizingAxis(new SizingMinMax(min, max), 0, SizingType.FIT);
    }

    public static SizingAxis fit() {
        return fit(0, Float.MAX_VALUE);
    }

    public static SizingAxis grow(float min, float max) {
        return new SizingAxis(new SizingMinMax(min, max), 0, SizingType.GROW);
    }

    public static SizingAxis grow() {
        return grow(0, Float.MAX_VALUE);
    }

    public static SizingAxis percent(float value) {
        return new SizingAxis(null, value, SizingType.PERCENT);
    }

    public static SizingAxis fixed(float value) {
        return new SizingAxis(new SizingMinMax(value, value), 0, SizingType.FIXED);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private SizingMinMax minMax;
        private float percent;
        private SizingType type;

        public Builder minMax(SizingMinMax minMax) {
            this.minMax = minMax;
            return this;
        }

        public Builder percent(float percent) {
            this.percent = percent;
            return this;
        }

        public Builder type(SizingType type) {
            this.type = type;
            return this;
        }

        public SizingAxis build() {
            return new SizingAxis(minMax, percent, type);
        }
    }
}
