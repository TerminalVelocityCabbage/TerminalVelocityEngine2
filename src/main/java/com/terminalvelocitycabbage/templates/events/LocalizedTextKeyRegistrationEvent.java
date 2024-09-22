package com.terminalvelocitycabbage.templates.events;

import com.terminalvelocitycabbage.engine.TerminalVelocityEngine;
import com.terminalvelocitycabbage.engine.event.RegistryEvent;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.registry.Registry;
import com.terminalvelocitycabbage.engine.registry.RegistryPair;

public class LocalizedTextKeyRegistrationEvent extends RegistryEvent<String> {

    public static final Identifier EVENT = TerminalVelocityEngine.identifierOf("LocalizedTextKeyRegistrationEvent");

    public LocalizedTextKeyRegistrationEvent(Registry<String> registry) {
        super(EVENT, registry);
    }

    public RegistryPair<String> registerKey(String namespace, String translationKey) {
        return register(new Identifier(namespace, translationKey), translationKey);
    }
}
