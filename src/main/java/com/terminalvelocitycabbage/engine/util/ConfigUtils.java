package com.terminalvelocitycabbage.engine.util;

import java.util.ArrayList;
import java.util.List;

public class ConfigUtils {

    public static float[] numberListToFloatArray(List<Number> list) {
        if (list == null) return new float[0];
        List<Float> convertedList = new ArrayList<>();
        list.forEach(n -> convertedList.add(n.floatValue()));
        return ArrayUtils.floatArrayFromClassList(convertedList);
    }

    public static int[] numberListToIntArray(List<Number> list) {
        if (list == null) return new int[0];
        List<Integer> convertedList = new ArrayList<>();
        list.forEach(n -> convertedList.add(n.intValue()));
        return ArrayUtils.intArrayFromClassList(convertedList);
    }

}
