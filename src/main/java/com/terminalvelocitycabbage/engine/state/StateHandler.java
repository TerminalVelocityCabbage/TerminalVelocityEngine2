package com.terminalvelocitycabbage.engine.state;

import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.tvevents.EventBus;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.templates.events.StateChangedEvent;

import java.util.HashMap;
import java.util.Map;

public class StateHandler {

    private final EventBus eventBus;
    public Map<Identifier, State<?>> states;

    public StateHandler(EventBus eventBus) {
        this.eventBus = eventBus;
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
        eventBus.publish(new StateChangedEvent<>(state)).now();
    }

}
