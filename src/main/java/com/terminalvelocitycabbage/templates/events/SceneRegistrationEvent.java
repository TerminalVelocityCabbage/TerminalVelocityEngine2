package com.terminalvelocitycabbage.templates.events;

import com.terminalvelocitycabbage.engine.TerminalVelocityEngine;
import com.terminalvelocitycabbage.engine.client.scene.Scene;
import com.terminalvelocitycabbage.engine.event.RegistryEvent;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.registry.Registry;

public class SceneRegistrationEvent extends RegistryEvent<Scene> {

    public static final Identifier EVENT = new Identifier(TerminalVelocityEngine.ID, "SceneRegistrationEvent");

    public SceneRegistrationEvent(Registry<Scene> registry) {
        super(EVENT, registry);
    }
}
