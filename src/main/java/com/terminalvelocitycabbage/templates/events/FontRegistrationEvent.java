package com.terminalvelocitycabbage.templates.events;

import com.terminalvelocitycabbage.engine.TerminalVelocityEngine;
import com.terminalvelocitycabbage.engine.client.renderer.Font;
import com.terminalvelocitycabbage.engine.event.RegistryEvent;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.registry.Registry;

public class FontRegistrationEvent extends RegistryEvent<Font> {

    public static final Identifier EVENT = TerminalVelocityEngine.identifierOf("FontRegistrationEvent");

    public FontRegistrationEvent(Registry<Font> registry) {
        super(EVENT, registry);
    }
}
