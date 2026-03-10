package com.terminalvelocitycabbage.engine.filesystem.resources.types;

import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.filesystem.resources.Resource;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryUtil;

import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
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
        try (InputStream is = openStream()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            Log.crash("Could not read URL Resource as String: " + url, e);
            return null;
        }
    }

    @Override
    public ByteBuffer asByteBuffer(boolean keepAlive) {

        ByteBuffer buffer = null;

        try (InputStream is = openStream()) {
            byte[] bytes = is.readAllBytes();
            if (keepAlive) {
                buffer = MemoryUtil.memCalloc(bytes.length);
            } else {
                buffer = BufferUtils.createByteBuffer(bytes.length);
            }
            buffer.put(bytes);
        } catch (IOException e) {
            Log.crash("Could not read URL as ByteBuffer: " + url, e);
        }

        if (buffer != null) {
            buffer.flip();
        } else {
            Log.crash("Could not get this URL Resource as a ByteBuffer: " + url, new RuntimeException());
        }

        return buffer;
    }

    public void printPath() {
        Log.info(url.toString());
    }
}
