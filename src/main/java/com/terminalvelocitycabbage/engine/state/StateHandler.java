package com.terminalvelocitycabbage.engine.state;

import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.event.EventDispatcher;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.templates.events.StateChangedEvent;

import java.util.HashMap;
import java.util.Map;

public class StateHandler {

    private final EventDispatcher eventDispatcher;
    public Map<Identifier, State<?>> states;

    public StateHandler(EventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
        this.states = new HashMap<>();
    }

    public void addState(Identifier identifier, Object initialValue) {
        states.put(identifier, new State<>(initialValue));
    }

    public <T> State<T> getState(Identifier identifier) {
        if (!states.containsKey(identifier)) Log.crash("Could not find state with identifier " + identifier + " in StateHandler");
        return (State<T>) states.get(identifier);
    }

    public <T> void updateState(Identifier identifier, T value) {
        State<T> state = getState(identifier);
        state.setValue(value);
        eventDispatcher.dispatchEvent(new StateChangedEvent<>(state));
    }

}
