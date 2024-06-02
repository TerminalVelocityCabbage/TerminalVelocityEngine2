package com.terminalvelocitycabbage.engine.util;

import java.util.List;

public class ArrayUtils {

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

}
