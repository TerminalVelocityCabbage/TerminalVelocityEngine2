package com.terminalvelocitycabbage.engine.util;

import org.joml.Vector2i;

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
    public static float lerp2d(float x1, float y1, float x2, float y2, float progress) {
        return y1 + ((progress - x1) * (y2 - y1)) / (x2 - x1);
    }

    /**
     * @param startValue the start value of the line we're lerping
     * @param endValue the end value of the line we're lerping
     * @param fraction the progress between these values
     * @return a number at the progress given between these two values
     */
    public static float lerp(float startValue, float endValue, float fraction) {
        return startValue + (endValue - startValue) * fraction;
    }

    /**
     * @param n the number to check if it's a power of 2
     * @return a boolean for if the given number is a power of 2
     */
    public static boolean isPowerOfTwo(int n) {
        return (n > 0) && ((n & (n - 1)) == 0);
    }

    /**
     * for the given area return the minimum dimensions for the integer sizes for that area
     * @param minArea The area of the rectangle needed
     * @return A {@link Vector2i} representing the minimized x and y dimensions of a rectangle that has an area at least
     * the area of the given minArea
     */
    public static Vector2i findMostSquareDimensions(int minArea) {
        int bestWidth = 1;
        int bestHeight = minArea;
        int minDifference = Integer.MAX_VALUE;

        // Try widths from sqrt(minArea) down to 1
        for (int width = (int) Math.sqrt(minArea); width >= 1; width--) {
            int height = (int) Math.ceil((double) minArea / width);
            int area = width * height;
            int difference = Math.abs(width - height);

            if (area >= minArea && difference < minDifference) {
                bestWidth = width;
                bestHeight = height;
                minDifference = difference;
            }
        }

        return new Vector2i(bestWidth, bestHeight);
    }

}
