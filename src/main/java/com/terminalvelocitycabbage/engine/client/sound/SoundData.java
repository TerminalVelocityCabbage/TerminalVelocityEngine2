package com.terminalvelocitycabbage.engine.client.sound;

import com.terminalvelocitycabbage.engine.client.ClientBase;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceCategory;
import com.terminalvelocitycabbage.engine.registry.Identifiable;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.stb.STBVorbis.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class SoundData implements Identifiable {

    private final int soundID;
    private final Identifier identifier;

    private ShortBuffer pcm;
    private final boolean stereo;
    private final int sampleRate;

    public SoundData(Identifier identifier) {

        this.identifier = identifier;

        //Allocate a pointer for this sound
        this.soundID = alGenBuffers();

        //Load its resource into something we can read
        ByteBuffer vorbis = ClientBase.getInstance().getFileSystem().getResource(ResourceCategory.SOUND, identifier).asByteBuffer();

        //Read the ogg format into PCM, so we can use it with OPENAL
        try (STBVorbisInfo info = STBVorbisInfo.malloc()){
            pcm = readVorbis(vorbis, info);
            stereo = info.channels() != 1;
            sampleRate = info.sample_rate();
        }

        alBufferData(soundID, stereo ? AL_FORMAT_STEREO16 : AL_FORMAT_MONO16, pcm, sampleRate);
    }

    private ShortBuffer readVorbis(ByteBuffer vorbis, STBVorbisInfo info) {

        try (MemoryStack stack = MemoryStack.stackPush()) {

            //Try to decode the vorbis ByteBuffer
            IntBuffer error = stack.mallocInt(1);
            long decoder = stb_vorbis_open_memory(vorbis, error, null);
            if (decoder == NULL) {
                throw new RuntimeException("Failed to open Ogg Vorbis file. Error: " + error.get(0));
            }

            //Get some meta about the vorbis ByteBuffer
            stb_vorbis_get_info(decoder, info);
            int channels = info.channels();
            int lengthSamples = stb_vorbis_stream_length_in_samples(decoder);

            //Convert Everything and cleanup
            pcm = MemoryUtil.memAllocShort(lengthSamples);
            pcm.limit(stb_vorbis_get_samples_short_interleaved(decoder, channels, pcm) * channels);
            stb_vorbis_close(decoder);

            return pcm;
        }
    }

    public int getSoundID() {
        return soundID;
    }

    public void destroy() {
        alDeleteBuffers(this.soundID);
        if (pcm != null) {
            MemoryUtil.memFree(pcm);
        }
    }

    public boolean isStereo() {
        return stereo;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    @Override
    public Identifier getIdentifier() {
        return identifier;
    }
}