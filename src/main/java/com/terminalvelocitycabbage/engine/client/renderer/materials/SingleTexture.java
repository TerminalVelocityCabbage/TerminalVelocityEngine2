package com.terminalvelocitycabbage.engine.client.renderer.materials;

import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.registry.Identifier;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public interface SingleTexture {

    int getHeight();
    int getWidth();
    int getTextureID();

    default int generateOpenGLTexture(Identifier textureIdentifier, int width, int height, int components, ByteBuffer imageBuffer) {
        //get the location that this texture will be bound to
        int textureID = glGenTextures();

        //Error if it couldn't generate a texture
        if (textureID == 0) Log.crash("generated texture: " + textureIdentifier + " resulted in an ID of: " + textureID);

        //Do all the OpenGL stuff we gotta do
        glBindTexture(GL_TEXTURE_2D, textureID);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 2 - (width & 1));
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, imageBuffer);
        glGenerateMipmap(GL_TEXTURE_2D);

        return textureID;
    }

    /**
     * Binds this texture for rendering
     */
    default void bind() {
        glBindTexture(GL_TEXTURE_2D, getTextureID());
    }

    /**
     * deletes this texture from opengl when we're done with it
     */
    default void cleanup() {
        glDeleteTextures(getTextureID());
    }

}
