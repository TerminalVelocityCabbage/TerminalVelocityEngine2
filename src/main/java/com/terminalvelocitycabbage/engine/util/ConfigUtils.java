package com.terminalvelocitycabbage.engine.util;

import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class ConfigUtils {

    public static Vector3f numberListToVector3f(List<Number> list) {
        float[] numberArray = numberListToFloatArray(list);
        return new Vector3f(numberArray[0], numberArray[1], numberArray[2]);
    }

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
