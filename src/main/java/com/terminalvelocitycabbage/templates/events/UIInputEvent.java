package com.terminalvelocitycabbage.templates.events;

import com.terminalvelocitycabbage.engine.event.Event;
import com.terminalvelocitycabbage.engine.registry.Identifier;

public abstract class UIInputEvent extends Event {
    public UIInputEvent(Identifier name) {
        super(name);
    }
}
