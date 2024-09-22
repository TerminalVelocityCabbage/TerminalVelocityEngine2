package com.terminalvelocitycabbage.engine.event;

import com.terminalvelocitycabbage.engine.registry.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class EventDispatcher {

	private static final Map<Identifier, ArrayList<Consumer<Event>>> eventListeners = new HashMap<>();

	/**
	 * Dispatches an event with an event object as context
	 * @param event The event that will be passed to all event listeners as context for the event
	 */
	public void dispatchEvent(Event event) {
		if (!eventListeners.containsKey(event.getId())) return;
		for (Consumer<Event> consumer : eventListeners.get(event.getId())) {
			consumer.accept(event);
		}
	}

	/**
	 * @param eventIdentifier The identifier of the event that you want to listen to
	 * @param event The event consumer which will be executed when the event is called
	 */
	public void listenToEvent(Identifier eventIdentifier, Consumer<Event> event) {
		eventListeners.putIfAbsent(eventIdentifier, new ArrayList<>());
		eventListeners.get(eventIdentifier).add(event);
    }
}
