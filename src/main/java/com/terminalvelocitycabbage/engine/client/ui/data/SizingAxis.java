package com.terminalvelocitycabbage.engine.client.ui.data;

import com.terminalvelocitycabbage.engine.client.ui.UI;

public record SizingAxis(SizingMinMax minMax, float percent, UI.SizingType type) {

    public static SizingAxis fit(float min, float max) {
        return new SizingAxis(new SizingMinMax(min, max), 0, UI.SizingType.FIT);
    }

    public static SizingAxis fit() {
        return fit(0, Float.MAX_VALUE);
    }

    public static SizingAxis grow(float min, float max) {
        return new SizingAxis(new SizingMinMax(min, max), 0, UI.SizingType.GROW);
    }

    public static SizingAxis grow() {
        return grow(0, Float.MAX_VALUE);
    }

    public static SizingAxis percent(float value) {
        return new SizingAxis(null, value, UI.SizingType.PERCENT);
    }

    public static SizingAxis fixed(float value) {
        return new SizingAxis(new SizingMinMax(value, value), 0, UI.SizingType.FIXED);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private SizingMinMax minMax;
        private float percent;
        private UI.SizingType type;

        public Builder minMax(SizingMinMax minMax) {
            this.minMax = minMax;
            return this;
        }

        public Builder percent(float percent) {
            this.percent = percent;
            return this;
        }

        public Builder type(UI.SizingType type) {
            this.type = type;
            return this;
        }

        public SizingAxis build() {
            return new SizingAxis(minMax, percent, type);
        }
    }
}
