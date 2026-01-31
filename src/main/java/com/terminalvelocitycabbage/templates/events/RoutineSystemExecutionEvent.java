package com.terminalvelocitycabbage.templates.events;

import com.terminalvelocitycabbage.engine.TerminalVelocityEngine;
import com.terminalvelocitycabbage.engine.ecs.Manager;
import com.terminalvelocitycabbage.engine.event.Event;
import com.terminalvelocitycabbage.engine.registry.Identifier;

public class RoutineSystemExecutionEvent extends Event {

    Manager manager;
    long deltaTime;

    public RoutineSystemExecutionEvent(Identifier name, Manager manager, long deltaTime) {
        super(name);
        this.manager = manager;
        this.deltaTime = deltaTime;
    }

    //TODO figure out a way to cache these identifiers since this might get really slow really fast
    public static Identifier pre(Identifier systemIdentifier) {
        return TerminalVelocityEngine.identifierOf("event", "routine_system_execution_pre-[" + systemIdentifier.toString() + "]");
    }

    public static Identifier post(Identifier systemIdentifier) {
        return TerminalVelocityEngine.identifierOf("event", "routine_system_execution_post-[" + systemIdentifier.toString() + "]");
    }

    public Manager getManager() {
        return manager;
    }

    public long getDeltaTime() {
        return deltaTime;
    }
}
