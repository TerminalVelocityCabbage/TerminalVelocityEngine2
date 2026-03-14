package com.terminalvelocitycabbage.engine.client.renderer.materials;

import com.terminalvelocitycabbage.engine.debug.Log;

import static org.lwjgl.opengl.GL11.*;

/**
 * A texture that is meant to be rendered to by an FBO
 */
public class RenderTexture extends Texture {

    private int internalFormat;
    private int format;
    private int type;

    /**
     * @param width The width of this texture in pixels
     * @param height The height of this texture in pixels
     */
    public RenderTexture(int width, int height) {
        this(width, height, GL_RGBA, GL_RGBA, GL_UNSIGNED_BYTE);
    }

    public RenderTexture(int width, int height, int internalFormat, int format, int type) {
        this.width = width;
        this.height = height;
        this.internalFormat = internalFormat;
        this.format = format;
        this.type = type;
    }

    public void init() {
        if (textureID != 0) return;

        //get the location that this texture will be bound to
        this.textureID = glGenTextures();

        if (this.textureID == 0) Log.crash("texture generation failed");

        //Do all the OpenGL stuff we gotta do
        glBindTexture(GL_TEXTURE_2D, textureID);
        glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0, format, type, (java.nio.ByteBuffer) null);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);

        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void setDimensions(int width, int height) {
        this.width = width;
        this.height = height;
    }
    @Override
    public int getTextureID() {
        init();
        return super.getTextureID();
    }

    @Override
    public void bind() {
        init();
        super.bind();
    }
}
