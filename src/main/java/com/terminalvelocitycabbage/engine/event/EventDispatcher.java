package com.terminalvelocitycabbage.engine.event;

import com.terminalvelocitycabbage.engine.registry.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class EventDispatcher {

	private static final Map<Identifier, Consumer<Event>> eventListeners = new HashMap<>();

	public void dispatchEvent(Event event) {
		for (Consumer<Event> payload : eventListeners.values()) {
			payload.accept(event);
		}
	}

	public void listenToEvent(Identifier eventIdentifier, Consumer<Event> event) {
		eventListeners.put(eventIdentifier, event);
	}
}
