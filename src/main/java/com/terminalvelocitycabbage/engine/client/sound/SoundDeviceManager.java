package com.terminalvelocitycabbage.engine.client.sound;

import com.terminalvelocitycabbage.engine.debug.Log;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.openal.AL.createCapabilities;
import static org.lwjgl.openal.ALC10.*;

public class SoundDeviceManager {

    private long device = -99;
    private long context = -99;

    public void init() {
        this.device = alcOpenDevice((ByteBuffer) null);
        if (device == -99) {
            Log.crash("OpenAL Device Error", new IllegalStateException("Failed to open the default OpenAL device."));
        }
        ALCCapabilities deviceCaps = ALC.createCapabilities(device);
        this.context = alcCreateContext(device, (IntBuffer) null);
        if (context == -99) {
            Log.crash("OpenAL Context Error", new IllegalStateException("Failed to create OpenAL context."));
        }
        alcMakeContextCurrent(context);
        createCapabilities(deviceCaps);
    }

    public void cleanup() {
        alcDestroyContext(context);
        alcCloseDevice(device);
    }

}