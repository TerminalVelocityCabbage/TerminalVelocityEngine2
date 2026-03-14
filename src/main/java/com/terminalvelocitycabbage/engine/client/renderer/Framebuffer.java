package com.terminalvelocitycabbage.engine.client.renderer;

import com.terminalvelocitycabbage.engine.client.ClientBase;
import com.terminalvelocitycabbage.engine.client.renderer.materials.RenderTexture;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.registry.Identifier;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL30.*;

public class Framebuffer {

    private int fboId;
    private int rboId;
    private final Map<Identifier, RenderTexture> textures = new LinkedHashMap<>();
    private final List<Identifier> textureIds = new ArrayList<>();
    private RenderTexture depthTexture;
    private Identifier depthTextureId;
    private boolean useDepthTexture;
    private int width;
    private int height;
    private boolean resized;

    public Framebuffer(int width, int height) {
        this(width, height, (Identifier[]) null);
    }

    public Framebuffer(int width, int height, Identifier... textureIds) {
        this(width, height, true, textureIds);
    }

    public Framebuffer(int width, int height, boolean useDepthTexture, Identifier... textureIds) {
        this.width = width;
        this.height = height;

        if (textureIds != null) {
            this.textureIds.addAll(List.of(textureIds));
        }
        this.useDepthTexture = useDepthTexture;
    }

    public Framebuffer(int width, int height, Identifier depthTextureId, Identifier... textureIds) {
        this.width = width;
        this.height = height;

        if (textureIds != null) {
            this.textureIds.addAll(List.of(textureIds));
        }
        this.depthTextureId = depthTextureId;
        this.useDepthTexture = true;
    }

    public void init() {
        if (fboId != 0) return;

        fboId = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fboId);

        textures.clear();
        for (Identifier id : textureIds) {
            var texture = ClientBase.getInstance().getTextureCache().getTexture(id);
            if (texture instanceof RenderTexture tex) {
                textures.put(id, tex);
            } else {
                Log.error("Texture " + id + " is not a RenderTexture!");
            }
        }

        if (textures.isEmpty() && textureIds.isEmpty()) {
            var tex = new RenderTexture(width, height);
            textures.put(new Identifier("engine", "texture", "color0"), tex);
        }

        int[] attachments = new int[textures.size()];
        int i = 0;
        for (RenderTexture tex : textures.values()) {
            tex.setDimensions(width, height);
            tex.init();
            attachments[i] = GL_COLOR_ATTACHMENT0 + i;
            glFramebufferTexture2D(GL_FRAMEBUFFER, attachments[i], GL_TEXTURE_2D, tex.getTextureID(), 0);
            i++;
        }

        if (attachments.length > 0) {
            glDrawBuffers(attachments);
        }

        if (useDepthTexture) {
            if (depthTextureId != null) {
                var texture = ClientBase.getInstance().getTextureCache().getTexture(depthTextureId);
                if (texture instanceof RenderTexture tex) {
                    depthTexture = tex;
                } else {
                    Log.error("Depth texture " + depthTextureId + " is not a RenderTexture!");
                    depthTexture = new RenderTexture(width, height, GL_DEPTH_COMPONENT, GL_DEPTH_COMPONENT, GL_FLOAT);
                }
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
        this.resized = true;
        cleanup();
        init();
    }

    public boolean isResized() {
        return resized;
    }

    public void resetResized() {
        resized = false;
    }

    public void bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, fboId);
    }

    public void unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public RenderTexture getTexture() {
        return textures.values().stream().findFirst().orElse(null);
    }

    public RenderTexture getTexture(int index) {
        return (RenderTexture) textures.values().toArray()[index];
    }

    public RenderTexture getTexture(Identifier identifier) {
        return textures.get(identifier);
    }

    public RenderTexture getDepthTexture() {
        return depthTexture;
    }

    public void cleanup() {
        glDeleteFramebuffers(fboId);
        if (rboId != 0) glDeleteRenderbuffers(rboId);
        textures.values().forEach(RenderTexture::cleanup);
        if (depthTexture != null) depthTexture.cleanup();
        fboId = 0;
        rboId = 0;
        textures.clear();
        depthTexture = null;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
