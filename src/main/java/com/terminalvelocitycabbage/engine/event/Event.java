package com.terminalvelocitycabbage.engine.event;

import com.terminalvelocitycabbage.engine.registry.Identifiable;
import com.terminalvelocitycabbage.engine.registry.Identifier;

/**
 * A class which represents some event that can be listened to
 */
public abstract class Event implements Identifiable, com.terminalvelocitycabbage.tvevents.Event {

	private final Identifier id;

	public Event(Identifier name) {
		this.id = name;
	}

	@Override
	public Identifier getIdentifier() {
		return id;
	}
}
