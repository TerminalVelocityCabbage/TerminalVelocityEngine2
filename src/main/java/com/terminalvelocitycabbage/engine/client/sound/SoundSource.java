package com.terminalvelocitycabbage.engine.client.sound;

import com.terminalvelocitycabbage.engine.client.ClientBase;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import org.joml.Vector3f;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.AL11.AL_SEC_OFFSET;

public class SoundSource {

    private final int sourceID;

    public SoundSource() {
        this.sourceID = alGenSources();
        alSourcei(sourceID, AL_LOOPING, AL_FALSE);
        alSourcei(sourceID, AL_SOURCE_RELATIVE, AL_TRUE);
    }

    public SoundSource setSound(Identifier soundIdentifier) {
        var sound = ClientBase.getInstance().getSoundManager().getSound(soundIdentifier).getSoundID();
        alSourcei(sourceID, AL_BUFFER, sound);
        return this;
    }

    public void play() {
        alSourcePlay(sourceID);
    }

    public void pause() {
        alSourcePause(sourceID);
    }

    public void stop() {
        alSourceStop(sourceID);
    }

    public boolean isPlaying() {
        return alGetSourcei(sourceID, AL_SOURCE_STATE) == AL_PLAYING;
    }

    public void destroy() {
        stop();
        alDeleteSources(sourceID);
    }

    public SoundSource setLooping(boolean loop) {
        alSourcei(sourceID, AL_LOOPING, loop ? AL_TRUE : AL_FALSE);
        return this;
    }

    public SoundSource setRelative(boolean relative) {
        alSourcei(sourceID, AL_SOURCE_RELATIVE, relative ? AL_TRUE : AL_FALSE);
        return this;
    }

    /**
     * @param degreeAngle The inner cone angle in degrees
     *                    Range: [0.0f, 360.0f]
     */
    public SoundSource setConeInnerAngle(float degreeAngle) {
        if (degreeAngle < 0.0f || degreeAngle > 360.0f) {
            Log.warn("Tried to set a cone inner angle value " + degreeAngle + " out of range 0.0f - 360.0f. We applied a modulo your value.");
            degreeAngle = degreeAngle % 360.0f;
        }
        alSourcef(sourceID, AL_CONE_INNER_ANGLE, degreeAngle);
        return this;
    }

    /**
     * @param degreeAngle The outer cone angle in degrees
     *                    Range: [0.0f, 360.0f]
     */
    public SoundSource setConeOuterAngle(float degreeAngle) {
        if (degreeAngle < 0.0f || degreeAngle > 360.0f) {
            Log.warn("Tried to set a cone outer angle value " + degreeAngle + " out of range 0.0f - 360.0f. We applied a modulo your value.");
            degreeAngle = degreeAngle % 360.0f;
        }
        alSourcef(sourceID, AL_CONE_OUTER_ANGLE, degreeAngle);
        return this;
    }

    /**
     * @param gain Set the gain of the outer cone relative to the inner cone (controlled by #setGain)
     *             Range: (Logarithmic) [0.0f, 1.0f]
     */
    public SoundSource setOuterConeGain(float gain) {
        if (gain < 0.0f || gain > 1.0f) {
            Log.warn("Tried to set an outer cone gain value " + gain + " out of range 0.0f - 1.0f We clamped your value.");
            gain = Math.max(0.0f, Math.min(1.0f, gain));
        }
        alSourcef(sourceID, AL_CONE_OUTER_GAIN, gain);
        return this;
    }

    /**
     * @param gain The gain applied.
     *             Range: [0.0f, Infinity]
     *             This is weird in OpenAl. it's not actually Logarithmic, it's on a wrong decibel scale.
     *             Every multiplication of 2 increases +6dB and every division by 2 decreases by -6dB
     *             0.0f means that there will be no sound played.
     */
    public SoundSource setGain(float gain) {
        if (gain < 0.0f) {
            Log.warn("Tried to set a gain value " + gain + " out of range >= 0.0f We clamped your value.");
            gain = 0.0f;
        }
        alSourcef(sourceID, AL_GAIN, gain);
        return this;
    }

    /**
     * @param distance The distance above which sources are no longer attenuated.
     *                 Range: [0.0f, Infinity]
     */
    public SoundSource setMaxDistance(float distance) {
        if (distance < 0.0f) {
            Log.warn("Tried to set a distance value " + distance + " out of range >= 0.0f We clamped your value.");
            distance = 0.0f;
        }
        alSourcef(sourceID, AL_MAX_DISTANCE, distance);
        return this;
    }

    /**
     * @param gain Set the maximum source attenuation
     *             Range: (Logarithmic) [0.0f, 1.0f]
     */
    public SoundSource setMaxGain(float gain) {
        if (gain < 0.0f || gain > 1.0f) {
            Log.warn("Tried to set a max gain value " + gain + " out of range 0.0f - 1.0f We clamped your value.");
            gain = Math.max(0.0f, Math.min(1.0f, gain));
        }
        alSourcef(sourceID, AL_MAX_GAIN, gain);
        return this;
    }

    /**
     * @param gain Set the minimum source attenuation
     *             Range: (Logarithmic) [0.0f, 1.0f]
     */
    public SoundSource setMinGain(float gain) {
        if (gain < 0.0f || gain > 1.0f) {
            Log.warn("Tried to set a min gain value " + gain + " out of range 0.0f - 1.0f We clamped your value.");
            gain = Math.max(0.0f, Math.min(1.0f, gain));
        }
        alSourcef(sourceID, AL_MIN_GAIN, gain);
        return this;
    }

    /**
     * @param pitch Pitch modification to be applied.
     *              Range: [0.0f, 2.0f]
     */
    public SoundSource setPitch(float pitch) {
        if (pitch < 0.5f || pitch > 2.0f) {
            Log.warn("Tried to set a pitch value " + pitch + " out of range 0.5f - 2.0f We clamped your value.");
            pitch = Math.max(0.5f, Math.min(2.0f, pitch));
        }
        alSourcef(sourceID, AL_PITCH, pitch);
        return this;
    }

    /**
     * @param distance Source-specific distance to reference for attenuation.
     *                 At 0.0f, no attenuation occurs.
     *                 Range: [0.0f, Infinity]
     */
    public SoundSource setReferenceDistance(float distance) {
        if (distance < 0.0f) {
            Log.warn("Tried to set a reference distance value " + distance + " out of range >= 0.0f We clamped your value.");
            distance = 0.0f;
        }
        alSourcef(sourceID, AL_REFERENCE_DISTANCE, distance);
        return this;
    }

    /**
     * @param rolloffFactor source-specific rolloff factor.
     *                      Range: [0.0f, Infinity]
     */
    public SoundSource setRolloffFactor(float rolloffFactor) {
        if (rolloffFactor < 0.0f) {
            Log.warn("Tried to set a rolloff factor value " + rolloffFactor + " out of range >= 0.0f. We clamped your value.");
            rolloffFactor = 0.0f;
        }
        alSourcef(sourceID, AL_ROLLOFF_FACTOR, rolloffFactor);
        return this;
    }

    /**
     * @param secOffset the number in seconds to set the current playback time to
     */
    public SoundSource setSecOffset(float secOffset) {
        alSourcef(sourceID, AL_SEC_OFFSET, secOffset);
        return this;
    }

    /**
     * @param direction The direction that the cones (if not 360 degrees) are facing.
     */
    public SoundSource setDirection(Vector3f direction) {
        alSource3f(sourceID, AL_DIRECTION, direction.x, direction.y, direction.z);
        return this;
    }

    /**
     * @param position The position of this sound source.
     */
    public SoundSource setPosition(Vector3f position) {
        alSource3f(sourceID, AL_POSITION, position.x, position.y, position.z);
        return this;
    }

    /**
     * @param velocity a vector defining the velocity of this sound source.
     *                 This is used to simulate the doppler effect of sounds.
     */
    public SoundSource setVelocity(Vector3f velocity) {
        alSource3f(sourceID, AL_VELOCITY, velocity.x, velocity.y, velocity.z);
        return this;
    }

}