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

    public Identifier registerFramebuffer(String namespace, String name, int width, int height) {
        return registerFramebuffer(namespace, name, null, width, height);
    }

    public Identifier registerFramebuffer(String namespace, String name, Identifier textureId, int width, int height) {
        Identifier identifier = new Identifier(namespace, "framebuffer", name);
        registry.register(identifier, new Framebuffer(width, height, textureId));
        return identifier;
    }

}
