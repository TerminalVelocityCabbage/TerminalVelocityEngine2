package com.terminalvelocitycabbage.templates.events;

import com.terminalvelocitycabbage.engine.TerminalVelocityEngine;
import com.terminalvelocitycabbage.engine.client.scene.Scene;
import com.terminalvelocitycabbage.engine.event.RegistryEvent;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.registry.Registry;

public class SceneRegistrationEvent extends RegistryEvent<Scene> {

    public static final Identifier EVENT = TerminalVelocityEngine.identifierOf("event", "scene_registration");

    public SceneRegistrationEvent(Registry<Scene> registry) {
        super(EVENT, registry);
    }

    public Identifier registerScene(String namespace, String name, Scene scene) {
        return register(new Identifier(namespace, "scene", name), scene).getIdentifier();
    }
}
