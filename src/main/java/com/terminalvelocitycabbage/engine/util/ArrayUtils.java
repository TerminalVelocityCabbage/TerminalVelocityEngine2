package com.terminalvelocitycabbage.engine.util;

import java.util.List;

public class ArrayUtils {

    /**
     * Combines a list of float arrays into a single float array
     *
     * @param arrays a list of arrays to combine
     * @return A combined array
     */
    public static float[] combineFloatArrays(List<float[]> arrays) {

        int totalElements = 0;
        for (float[] array : arrays) {
            totalElements += array.length;
        }

        float[] compiledData = new float[totalElements];

        int currentPosition = 0;
        for (float[] array : arrays) {
            for (int i = 0; i < array.length; i++) {
                compiledData[currentPosition] = array[i];
                currentPosition++;
            }
        }

        return compiledData;
    }

    /**
     * @param list the float list to be converted to an array
     * @return an array made of the same values as the list
     */
    public static float[] floatArrayFromClassList(List<Float> list) {
        float[] compiledData = new float[list.size()];
        int currentPosition = 0;
        for (Float element : list) {
            compiledData[currentPosition] = element;
            currentPosition++;
        }
        return compiledData;
    }

    /**
     * @param list the float list to be converted to an array
     * @return an array made of the same values as the list
     */
    public static int[] intArrayFromClassList(List<Integer> list) {
        int[] compiledData = new int[list.size()];
        int currentPosition = 0;
        for (Integer element : list) {
            compiledData[currentPosition] = element;
            currentPosition++;
        }
        return compiledData;
    }
}
