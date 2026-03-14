package com.terminalvelocitycabbage.engine.client.renderer;

import com.terminalvelocitycabbage.engine.client.ClientBase;
import com.terminalvelocitycabbage.engine.client.renderer.materials.RenderTexture;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.registry.Identifier;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL30.*;

public class Framebuffer {

    private int fboId;
    private int rboId;
    private final List<RenderTexture> textures = new ArrayList<>();
    private final List<Identifier> textureIds = new ArrayList<>();
    private RenderTexture depthTexture;
    private Identifier depthTextureId;
    private boolean useDepthTexture;
    private int width;
    private int height;

    public Framebuffer(int width, int height) {
        this(width, height, (Identifier) null);
    }

    public Framebuffer(int width, int height, Identifier textureId) {
        this.width = width;
        this.height = height;
        if (textureId != null) {
            this.textureIds.add(textureId);
        }
    }

    public Framebuffer(int width, int height, List<Identifier> textureIds) {
        this(width, height, textureIds, null);
    }

    public Framebuffer(int width, int height, List<Identifier> textureIds, Identifier depthTextureId) {
        this.width = width;
        this.height = height;
        if (textureIds != null) {
            this.textureIds.addAll(textureIds);
        }
        this.depthTextureId = depthTextureId;
        this.useDepthTexture = depthTextureId != null;
    }

    public Framebuffer(int width, int height, List<Identifier> textureIds, boolean useDepthTexture) {
        this.width = width;
        this.height = height;
        if (textureIds != null) {
            this.textureIds.addAll(textureIds);
        }
        this.useDepthTexture = useDepthTexture;
    }

    public void init() {
        if (fboId != 0) return;

        fboId = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fboId);

        textures.clear();
        for (Identifier id : textureIds) {
            var tex = (RenderTexture) ClientBase.getInstance().getTextureCache().getTexture(id);
            textures.add(tex);
        }

        if (textures.isEmpty() && textureIds.isEmpty()) {
            var tex = new RenderTexture(width, height);
            textures.add(tex);
        }

        int[] attachments = new int[textures.size()];
        for (int i = 0; i < textures.size(); i++) {
            var tex = textures.get(i);
            tex.setDimensions(width, height);
            tex.init();
            attachments[i] = GL_COLOR_ATTACHMENT0 + i;
            glFramebufferTexture2D(GL_FRAMEBUFFER, attachments[i], GL_TEXTURE_2D, tex.getTextureID(), 0);
        }

        if (attachments.length > 0) {
            glDrawBuffers(attachments);
        }

        if (useDepthTexture) {
            if (depthTextureId != null) {
                depthTexture = (RenderTexture) ClientBase.getInstance().getTextureCache().getTexture(depthTextureId);
            } else {
                depthTexture = new RenderTexture(width, height, GL_DEPTH_COMPONENT, GL_DEPTH_COMPONENT, GL_FLOAT);
            }
            depthTexture.setDimensions(width, height);
            depthTexture.init();
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthTexture.getTextureID(), 0);
        } else {
            rboId = glGenRenderbuffers();
            glBindRenderbuffer(GL_RENDERBUFFER, rboId);
            glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, width, height);
            glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, rboId);
        }

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
        return getTexture(0);
    }

    public RenderTexture getTexture(int index) {
        return textures.get(index);
    }

    public RenderTexture getDepthTexture() {
        return depthTexture;
    }

    public void cleanup() {
        glDeleteFramebuffers(fboId);
        if (rboId != 0) glDeleteRenderbuffers(rboId);
        textures.forEach(RenderTexture::cleanup);
        if (depthTexture != null) depthTexture.cleanup();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
