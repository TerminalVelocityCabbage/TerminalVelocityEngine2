package com.terminalvelocitycabbage.engine.event;

import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.registry.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class EventDispatcher {

	private static final Map<Identifier, ArrayList<Consumer<Event>>> eventListeners = new HashMap<>();

	public void dispatchEvent(Event event) {
		if (!eventListeners.containsKey(event.getId())) return;
		for (Consumer<Event> consumer : eventListeners.get(event.getId())) {
			consumer.accept(event);
		}
	}

	public void listenToEvent(Identifier eventIdentifier, Consumer<Event> event) {
        eventListeners.putIfAbsent(eventIdentifier, new ArrayList<>());
		eventListeners.get(eventIdentifier).add(event);
    }
}
