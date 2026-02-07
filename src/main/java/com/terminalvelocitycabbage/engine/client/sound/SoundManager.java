package com.terminalvelocitycabbage.engine.client.sound;

import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.registry.Registry;

import java.util.ArrayList;
import java.util.List;

public class SoundManager {

    private SoundListener listener;
    private final Registry<SoundData> sounds;
    private final List<SoundSource> soundSources;

    public SoundManager() {
        listener = null;
        sounds = new Registry<>();
        soundSources = new ArrayList<>();
    }

    public void addSound(SoundData sound) {
        this.sounds.register(new Identifier(sound.getIdentifier().namespace(), "sound", sound.getIdentifier().name()), sound);
    }

    public SoundData getSound(Identifier sound) {
        return sounds.get(sound);
    }

    public SoundListener getListener() {
        return this.listener;
    }

    public void setListener(SoundListener listener) {
        if (this.listener != null) Log.warn("Overwriting existing sound listener, this is not recommended!");
        this.listener = listener;
    }

    public void addSoundSource(SoundSource soundSource) {
        soundSources.add(soundSource);
    }

    public void cleanup() {
        soundSources.forEach(SoundSource::destroy);
        sounds.getRegistryContents().values().forEach(SoundData::destroy);
    }

}