package com.terminalvelocitycabbage.templates.events;

import com.terminalvelocitycabbage.engine.TerminalVelocityEngine;
import com.terminalvelocitycabbage.engine.client.renderer.model.formats.TVAnimation;
import com.terminalvelocitycabbage.engine.event.RegistryEvent;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.registry.Registry;

public class TVAnimationRegistrationEvent extends RegistryEvent<TVAnimation> {

    public static final Identifier EVENT = TerminalVelocityEngine.identifierOf("event", "tv_animation_registration");

    public TVAnimationRegistrationEvent(Registry<TVAnimation> registry) {
        super(EVENT, registry);
    }

    public Identifier registerTVAnimation(String namespace, Identifier animationResource) {
        TVAnimation animation = TVAnimation.of(animationResource);
        return register(new Identifier(namespace, "tv_animation", animation.metadata().name()), animation).getIdentifier();
    }
}
