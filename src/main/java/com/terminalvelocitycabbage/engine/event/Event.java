package com.terminalvelocitycabbage.engine.event;

import com.terminalvelocitycabbage.engine.registry.Identifier;

/**
 * A class which represents some event that can be listened to
 */
public abstract class Event {

	private final Identifier id;

	public Event(Identifier name) {
		this.id = name;
	}

	/**
	 * @return The identifier of this event
	 */
	public Identifier getId() {
		return id;
	}
}
