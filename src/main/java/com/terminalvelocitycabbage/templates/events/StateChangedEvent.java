package com.terminalvelocitycabbage.templates.events;

import com.terminalvelocitycabbage.engine.TerminalVelocityEngine;
import com.terminalvelocitycabbage.engine.event.Event;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.state.State;

public class StateChangedEvent<T> extends Event {

    public static final Identifier EVENT = TerminalVelocityEngine.identifierOf("StateChangedEvent");

    private final State<T> state;

    public StateChangedEvent(State<T> state) {
        super(EVENT);
        this.state = state;
    }

    public State<T> getState() {
        return state;
    }
}
