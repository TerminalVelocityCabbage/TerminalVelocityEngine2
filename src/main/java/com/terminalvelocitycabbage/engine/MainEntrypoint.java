package com.terminalvelocitycabbage.engine;

import com.terminalvelocitycabbage.engine.event.EventDispatcher;

public abstract class MainEntrypoint extends Entrypoint {

    protected final EventDispatcher eventDispatcher;

    public MainEntrypoint(String namespace) {
        super(namespace);
        this.eventDispatcher = new EventDispatcher();
    }

    public EventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }
}
