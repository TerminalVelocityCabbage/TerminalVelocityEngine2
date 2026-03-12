package com.terminalvelocitycabbage.engine.client.renderer;

import com.terminalvelocitycabbage.engine.client.ClientBase;
import com.terminalvelocitycabbage.engine.client.renderer.materials.RenderTexture;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.registry.Identifier;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class Framebuffer {

    private int fboId;
    private int rboId;
    private RenderTexture texture;
    private Identifier textureId;
    private int width;
    private int height;

    public Framebuffer(int width, int height) {
        this(width, height, null);
    }

    public Framebuffer(int width, int height, Identifier textureId) {
        this.width = width;
        this.height = height;
        this.textureId = textureId;
    }

    public void init() {
        if (fboId != 0) return;

        fboId = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fboId);

        if (textureId != null) {
            texture = (RenderTexture) ClientBase.getInstance().getTextureCache().getTexture(textureId);
        } else {
            texture = new RenderTexture(width, height);
        }
        texture.init();
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture.getTextureID(), 0);

        rboId = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, rboId);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, rboId);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            Log.crash("Framebuffer is not complete!");
        }

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void resize(int width, int height) {
        if (this.width == width && this.height == height) return;
        if (width <= 0 || height <= 0) return;
        this.width = width;
        this.height = height;
        cleanup();
        init();
    }

    public void bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, fboId);
    }

    public void unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public RenderTexture getTexture() {
        return texture;
    }

    public void cleanup() {
        glDeleteFramebuffers(fboId);
        glDeleteRenderbuffers(rboId);
        texture.cleanup();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
