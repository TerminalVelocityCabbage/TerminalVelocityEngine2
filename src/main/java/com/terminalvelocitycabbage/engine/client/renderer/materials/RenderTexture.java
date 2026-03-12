package com.terminalvelocitycabbage.engine.client.renderer.materials;

import com.terminalvelocitycabbage.engine.debug.Log;

import static org.lwjgl.opengl.GL11.*;

/**
 * A texture that is meant to be rendered to by an FBO
 */
public class RenderTexture extends Texture {

    /**
     * @param width The width of this texture in pixels
     * @param height The height of this texture in pixels
     */
    public RenderTexture(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void init() {
        if (textureID != 0) return;

        //get the location that this texture will be bound to
        this.textureID = glGenTextures();

        if (this.textureID == 0) Log.crash("texture generation failed");

        //Do all the OpenGL stuff we gotta do
        glBindTexture(GL_TEXTURE_2D, textureID);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (java.nio.ByteBuffer) null);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);

        glBindTexture(GL_TEXTURE_2D, 0);
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
