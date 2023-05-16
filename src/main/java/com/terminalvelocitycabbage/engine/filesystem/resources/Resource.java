package com.terminalvelocitycabbage.engine.filesystem.resources;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * A definition for the ways a resource needs to be parseable
 */
public interface Resource {

    InputStream openStream() throws IOException;

    DataInputStream asDataStream();

    default ByteBuffer asByteBuffer() {
        return asByteBuffer(false);
    }

    ByteBuffer asByteBuffer(boolean keepAlive);

    String asString();
}
