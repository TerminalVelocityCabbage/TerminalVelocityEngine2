package com.terminalvelocitycabbage.engine.client.ui.data;

import com.terminalvelocitycabbage.engine.client.ui.UI;

public record SizingAxis(float min, float max, float percent, UI.SizingType type) {

    public static SizingAxis fit() {
        return fit(0, Float.MAX_VALUE);
    }

    public static SizingAxis fit(float min, float max) {
        return new SizingAxis(min, max, 0, UI.SizingType.FIT);
    }

    public static SizingAxis grow() {
        return grow(0, Float.MAX_VALUE);
    }

    public static SizingAxis grow(float min, float max) {
        return new SizingAxis(min, max, 0, UI.SizingType.GROW);
    }

    public static SizingAxis percent(float value) {
        return new SizingAxis(0, Float.MAX_VALUE, value, UI.SizingType.PERCENT);
    }

    public static SizingAxis percent(int value) {
        return new SizingAxis(0, Float.MAX_VALUE, value / 100f, UI.SizingType.PERCENT);
    }

    public static SizingAxis fixed(float value) {
        return new SizingAxis(value, value, 0, UI.SizingType.FIXED);
    }
}
