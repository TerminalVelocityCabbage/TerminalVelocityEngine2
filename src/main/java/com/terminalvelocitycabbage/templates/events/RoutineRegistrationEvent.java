package com.terminalvelocitycabbage.templates.events;

import com.terminalvelocitycabbage.engine.TerminalVelocityEngine;
import com.terminalvelocitycabbage.engine.event.RegistryEvent;
import com.terminalvelocitycabbage.engine.graph.Routine;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.registry.Registry;

public class RoutineRegistrationEvent extends RegistryEvent<Routine> {

    public static final Identifier EVENT = TerminalVelocityEngine.identifierOf("RoutineRegistrationEvent");

    public RoutineRegistrationEvent(Registry<Routine> routineRegistry) {
        super(EVENT, routineRegistry);
    }
}
