package com.terminalvelocitycabbage.engine.event;

import com.terminalvelocitycabbage.engine.registry.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class EventDispatcher {

	private static final Map<Identifier, Consumer<Event>> eventListeners = new HashMap<>();

	public void dispatchEvent(Event event) {
		for (Map.Entry<Identifier, Consumer<Event>> entry : eventListeners.entrySet()) {
			if (entry.getKey().equals(event.getId())) {
				entry.getValue().accept(event);
			}
		}
	}

	public void listenToEvent(Identifier eventIdentifier, Consumer<Event> event) {
		eventListeners.put(eventIdentifier, event);
	}
}
