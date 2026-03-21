package com.terminalvelocitycabbage.engine.util;

import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.List;

public class ConfigUtils {

    public static Vector2i parseVector2i(List<Long> list) {
        if (list == null || list.size() < 2) return new Vector2i(0, 0);
        return new Vector2i(list.get(0).intValue(), list.get(1).intValue());
    }

    public static Vector3f parseVector3f(List<Number> list) {
        if (list == null || list.size() < 3) return new Vector3f(0, 0, 0);
        return new Vector3f(list.get(0).floatValue(), list.get(1).floatValue(), list.get(2).floatValue());
    }

    public static Vector3i parseVector3i(List<Long> list) {
        if (list == null || list.size() < 3) return new Vector3i(0, 0, 0);
        return new Vector3i(list.get(0).intValue(), list.get(1).intValue(), list.get(2).intValue());
    }

}
