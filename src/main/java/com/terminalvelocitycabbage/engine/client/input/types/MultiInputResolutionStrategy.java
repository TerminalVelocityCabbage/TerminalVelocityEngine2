package com.terminalvelocitycabbage.engine.client.input.types;

public enum MultiInputResolutionStrategy {

    FLOAT_MIN,
    FLOAT_MAX;

    public float resolve(float current, float input) {
        float result = current;
        result = switch (this) {
            case FLOAT_MIN -> Math.min(input, result);
            case FLOAT_MAX -> Math.max(input, result);
        };
        return result;
    }
}
