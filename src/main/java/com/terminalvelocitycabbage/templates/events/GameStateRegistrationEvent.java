package com.terminalvelocitycabbage.templates.events;

import com.terminalvelocitycabbage.engine.TerminalVelocityEngine;
import com.terminalvelocitycabbage.engine.event.Event;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.state.StateHandler;

public class GameStateRegistrationEvent extends Event {

    StateHandler stateHandler;

    public static final Identifier EVENT = TerminalVelocityEngine.identifierOf("event", "game_state_registration");

    public GameStateRegistrationEvent(StateHandler stateHandler) {
        super(EVENT);
        this.stateHandler = stateHandler;
    }

    public Identifier registerState(String namespace, String name) {
        var identifier = new Identifier(namespace, "state", name);
        stateHandler.addState(identifier, false);
        return identifier;
    }

    public Identifier registerState(String namespace, String name, Object value) {
        var identifier = new Identifier(namespace, "state", name);
        stateHandler.addState(identifier, value);
        return identifier;
    }
}
