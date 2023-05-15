package com.terminalvelocitycabbage.engine.filesystem.resources;

import com.terminalvelocitycabbage.engine.debug.Log;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryUtil;

import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class URLResource implements Resource {

    private final URL url;

    public URLResource(URL url) {
        this.url = url;
    }

    @Override
    public InputStream openStream() {
        try {
            return url.openStream();
        } catch (IOException e) {
            Log.crash("Resource Loading Error could not open Stream", new RuntimeException(e));
            return null;
        }
    }

    @Override
    public DataInputStream asDataStream() {
        return new DataInputStream(openStream());
    }

    @Override
    public String asString() {
        var isr = new InputStreamReader(openStream(), StandardCharsets.UTF_8);
        var br = new BufferedReader(isr);
        return br.lines().collect(Collectors.joining("\n"));
    }

    @Override
    public ByteBuffer asByteBuffer(boolean keepAlive) {

        ByteBuffer buffer = null;
        Path path = Paths.get(url.getPath().replaceFirst("/", "").replaceFirst("file:", ""));

        if (Files.isReadable(path)) {
            try(SeekableByteChannel sbc = Files.newByteChannel(path)) {
                if (keepAlive) {
                    buffer = MemoryUtil.memCalloc((int)sbc.size() + 1);
                } else {
                    buffer = BufferUtils.createByteBuffer((int)sbc.size() + 1);
                }
                while(sbc.read(buffer) != -1);
            } catch(IOException e) {
                Log.crash("Could not read byte channel", new IOException(e));
            }
        }

        if (buffer != null) {
            buffer.flip();
        } else {
            Log.crash("Could not get this URL Resource as a ByteBuffer", new RuntimeException());
        }

        return buffer;
    }

    public void printPath() {
        Path path = Paths.get(url.getPath().replaceFirst("/", "").replaceFirst("file:", ""));
        Log.info(path.toString());
    }
}
