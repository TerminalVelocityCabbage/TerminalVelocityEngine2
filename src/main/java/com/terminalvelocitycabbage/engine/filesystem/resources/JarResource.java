package com.terminalvelocitycabbage.engine.filesystem.resources;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class JarResource implements Resource {

    private final JarFile jarFile;
    private final JarEntry jarEntry;

    public JarResource(JarFile jarFile, JarEntry jarEntry) {
        this.jarFile = jarFile;
        this.jarEntry = jarEntry;
    }

    @Override
    public InputStream openStream() {
        try {
            return jarFile.getInputStream(jarEntry);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public DataInputStream asDataStream() {
        return new DataInputStream(openStream());
    }

    //TODO test
    @Override
    public ByteBuffer asByteBuffer(boolean keepAlive) {
        InputStream inputStream = openStream();
        try {
            ByteBuffer byteBuffer = ByteBuffer.allocate(inputStream.available());
            Channels.newChannel(inputStream).read(byteBuffer);
            return byteBuffer;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String asString() {
        var isr = new InputStreamReader(openStream(), StandardCharsets.UTF_8);
        var br = new BufferedReader(isr);
        return br.lines().collect(Collectors.joining("\n"));
    }
}
