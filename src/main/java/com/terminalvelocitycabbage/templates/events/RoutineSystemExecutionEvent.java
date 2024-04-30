package com.terminalvelocitycabbage.templates.events;

import com.terminalvelocitycabbage.engine.TerminalVelocityEngine;
import com.terminalvelocitycabbage.engine.ecs.Manager;
import com.terminalvelocitycabbage.engine.event.Event;
import com.terminalvelocitycabbage.engine.registry.Identifier;

public class RoutineSystemExecutionEvent extends Event {

    boolean enabled;
    Manager manager;

    public RoutineSystemExecutionEvent(Identifier name, Manager manager, boolean paused) {
        super(name);
        this.manager = manager;
        this.enabled = paused;
    }

    public static Identifier pre(Identifier systemIdentifier) {
        return TerminalVelocityEngine.identifierOf("routineSystemExecutionEventPre-[" + systemIdentifier.toString() + "]");
    }

    public static Identifier post(Identifier systemIdentifier) {
        return TerminalVelocityEngine.identifierOf("routineSystemExecutionEventPre-[" + systemIdentifier.toString() + "]");
    }

    public Manager getManager() {
        return manager;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
