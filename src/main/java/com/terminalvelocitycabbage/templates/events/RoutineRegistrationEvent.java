package com.terminalvelocitycabbage.templates.events;

import com.terminalvelocitycabbage.engine.TerminalVelocityEngine;
import com.terminalvelocitycabbage.engine.event.RegistryEvent;
import com.terminalvelocitycabbage.engine.graph.Routine;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.registry.Registry;

public class RoutineRegistrationEvent extends RegistryEvent<Routine> {

    public static final Identifier EVENT = TerminalVelocityEngine.identifierOf("event", "routine_registration");

    public RoutineRegistrationEvent(Registry<Routine> routineRegistry) {
        super(EVENT, routineRegistry);
    }

    public Identifier registerStep(String namespace, String name) {
        return new Identifier(namespace, "routine_step", name);
    }

    public Routine registerRoutine(String namespace, String name, Routine routine) {
        return register(new Identifier(namespace, "routine", name), routine).getElement();
    }
}
