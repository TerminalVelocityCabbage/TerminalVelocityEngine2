package com.terminalvelocitycabbage.templates.events;

import com.terminalvelocitycabbage.engine.TerminalVelocityEngine;
import com.terminalvelocitycabbage.engine.client.renderer.Framebuffer;
import com.terminalvelocitycabbage.engine.event.Event;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.registry.Registry;

public class FramebufferRegistrationEvent extends Event {

    public static final Identifier EVENT = TerminalVelocityEngine.identifierOf("event", "framebuffer_registration");

    private final Registry<Framebuffer> registry;

    public FramebufferRegistrationEvent(Registry<Framebuffer> registry) {
        super(EVENT);
        this.registry = registry;
    }

    public Identifier registerFramebuffer(String namespace, String name, int width, int height, Identifier... textureIds) {
        return registerFramebuffer(namespace, name, width, height, false, textureIds);
    }

    public Identifier registerFramebuffer(String namespace, String name, int width, int height, boolean useDepthTexture, Identifier... textureIds) {
        Identifier identifier = new Identifier(namespace, "framebuffer", name);
        registry.register(identifier, new Framebuffer(width, height, useDepthTexture, textureIds));
        return identifier;
    }

    public Identifier registerDepthFramebuffer(String namespace, String name, int width, int height, Identifier depthTextureId, Identifier... textureIds) {
        Identifier identifier = new Identifier(namespace, "framebuffer", name);
        registry.register(identifier, new Framebuffer(width, height, depthTextureId, textureIds));
        return identifier;
    }

}
