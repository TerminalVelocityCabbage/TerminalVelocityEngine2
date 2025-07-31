package com.terminalvelocitycabbage.engine.util;

public class MathUtils {

    /**
     * Performs linear interpolation to find the y-value corresponding to a given x-value.
     *
     * @param x1 The x-coordinate of the first known data point.
     * @param y1 The y-coordinate of the first known data point.
     * @param x2 The x-coordinate of the second known data point.
     * @param y2 The y-coordinate of the second known data point.
     * @param progress The x-value for which to interpolate the y-value.
     * @return The interpolated y-value.
     */
    public static float linearInterpolate(float x1, float y1, float x2, float y2, float progress) {
        return y1 + ((progress - x1) * (y2 - y1)) / (x2 - x1);
    }

}
