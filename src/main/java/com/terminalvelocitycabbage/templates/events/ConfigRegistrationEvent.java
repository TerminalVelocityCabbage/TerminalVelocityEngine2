package com.terminalvelocitycabbage.templates.events;

import com.terminalvelocitycabbage.engine.TerminalVelocityEngine;
import com.terminalvelocitycabbage.engine.event.RegistryEvent;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceLocation;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.registry.Registry;

public class ConfigRegistrationEvent extends RegistryEvent<ResourceLocation> {

    public static final Identifier EVENT = TerminalVelocityEngine.identifierOf("ConfigRegistrationEvent");

    public ConfigRegistrationEvent(Identifier name, Registry<ResourceLocation> registry) {
        super(name, registry);
    }
}
