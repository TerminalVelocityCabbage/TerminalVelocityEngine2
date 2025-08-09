package com.terminalvelocitycabbage.templates.events;

import com.terminalvelocitycabbage.engine.TerminalVelocityEngine;
import com.terminalvelocitycabbage.engine.event.Event;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.state.StateHandler;

public class GameStateRegistrationEvent extends Event {

    StateHandler stateHandler;

    public static final Identifier EVENT = TerminalVelocityEngine.identifierOf("GameStateRegistrationEvent");

    public GameStateRegistrationEvent(StateHandler stateHandler) {
        super(EVENT);
        this.stateHandler = stateHandler;
    }

    public Identifier registerState(Identifier stateIdentifier) {
        stateHandler.addState(stateIdentifier);
        return stateIdentifier;
    }

    public Identifier registerState(Identifier stateIdentifier, boolean enabled) {
        stateHandler.addState(stateIdentifier, enabled);
        return stateIdentifier;
    }
}
