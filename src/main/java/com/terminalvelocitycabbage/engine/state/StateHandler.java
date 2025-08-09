package com.terminalvelocitycabbage.engine.state;

import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.registry.Identifier;

import java.util.HashMap;
import java.util.Map;

public class StateHandler {

    public Map<Identifier, State> states;

    public StateHandler() {
        this.states = new HashMap<>();
    }

    public void addState(Identifier identifier) {
        addState(identifier, false);
    }

    public void addState(Identifier identifier, boolean enable) {
        states.put(identifier, new State(enable));
    }

    public State getState(Identifier identifier) {
        if (!states.containsKey(identifier)) Log.crash("Could not find state with identifier " + identifier + " in StateHandler");
        return states.get(identifier);
    }

    public void tick() {
        states.values().forEach(State::tick);
    }
}
