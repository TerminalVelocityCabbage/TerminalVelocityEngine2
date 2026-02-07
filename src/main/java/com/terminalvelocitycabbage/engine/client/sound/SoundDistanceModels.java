package com.terminalvelocitycabbage.engine.client.sound;

import static org.lwjgl.openal.AL10.*;

public enum SoundDistanceModels {

    INVERSE_DISTANCE("inverse_distance", AL_INVERSE_DISTANCE),
    INVERSE_DISTANCE_CLAMPED("inverse_distance_clamped", AL_INVERSE_DISTANCE_CLAMPED),
    NONE("none", AL_NONE);

    private String name;
    private int alModel;

    SoundDistanceModels(String name, int alModel) {
        this.name = name;
        this.alModel = alModel;
    }

    public String getName() {
        return name;
    }

    public int getAlModel() {
        return alModel;
    }
}