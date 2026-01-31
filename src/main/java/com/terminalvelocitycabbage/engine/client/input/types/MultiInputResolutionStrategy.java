package com.terminalvelocitycabbage.engine.client.input.types;

/**
 * A way to resolve multiple inputs in one control. Like a keyboard and controller input mapped tho the same control.
 * This SHOULD be a solution for an edge case, but it removes any undefined behavior which would result in a noisy
 * controller or something like that overriding earlier processed controls.
 */
public enum MultiInputResolutionStrategy {

    FLOAT_MIN,
    FLOAT_MAX;

    /**
     * @param current The previously processed value
     * @param input The value to compare this previous value to
     * @return A resolved value to be used as the latest status of the input process.
     */
    public float resolve(float current, float input) {
        float result = current;
        result = switch (this) {
            case FLOAT_MIN -> Math.min(input, result);
            case FLOAT_MAX -> Math.max(input, result);
        };
        return result;
    }
}
