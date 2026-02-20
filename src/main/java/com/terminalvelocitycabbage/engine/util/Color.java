package com.terminalvelocitycabbage.engine.util;

import com.terminalvelocitycabbage.engine.debug.Log;

public class Color {

    float r, g, b, a;

    public Color(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public Color(int r, int g, int b, int a) {
        this.r = r / 255f;
        this.g = g / 255f;
        this.b = b / 255f;
        this.a = a / 255f;
    }

    /**
     * @param hexColor the string in format #xxxxxx or #xxx to be parsed
     * @return a new Color object with the values derived from this hex code
     */
    public static Color ofHex(String hexColor) {
        float r = -1;
        float g = -1;
        float b = -1;
        if (hexColor.length() == 7) {
            r = Integer.valueOf( hexColor.substring( 1, 3 ), 16 ) / 255f;
            g = Integer.valueOf( hexColor.substring( 3, 5 ), 16 ) / 255f;
            b = Integer.valueOf( hexColor.substring( 5, 7 ), 16 ) / 255f;
        }
        if (hexColor.length() == 4) {
            r = (Integer.valueOf( hexColor.substring( 1, 2 ), 16 ) * 17) / 255f;
            g = (Integer.valueOf( hexColor.substring( 2, 3 ), 16 ) * 17) / 255f;
            b = (Integer.valueOf( hexColor.substring( 3, 4 ), 16 ) * 17) / 255f;
        }
        if (r == -1 || g == -1 || b == -1) {
            Log.crash("Could not parse hex color " + hexColor, new RuntimeException("Color must be in #xxxxxx or #xxx format"));
        }
        return new Color(r, g, b, 1f);
    }

    public void set(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public float r() {
        return r;
    }

    public float g() {
        return g;
    }

    public float b() {
        return b;
    }

    public float a() {
        return a;
    }

    public String toPropString() {
        return r + "," + g + "," + b + "," + a;
    }

    @Override
    public String toString() {
        return "Color{r=" + r + " g=" + g + " b=" + b + " a=" + a + "}";
    }
}
