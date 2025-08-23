package com.terminalvelocitycabbage.engine.event;

import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.util.touples.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class EventDispatcher {

	private static final Map<Identifier, ArrayList<Consumer<Event>>> eventListeners = new HashMap<>();
	private static final Map<UUID, Pair<Identifier, Consumer<Event>>> eventListenersByUUID = new HashMap<>();

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
	public UUID listenToEvent(Identifier eventIdentifier, Consumer<Event> event) {
		eventListeners.putIfAbsent(eventIdentifier, new ArrayList<>());
		eventListeners.get(eventIdentifier).add(event);
		UUID uuid = UUID.randomUUID();
		eventListenersByUUID.put(uuid, new Pair<>(eventIdentifier, event));
		return uuid;
    }

	/**
	 * Unsubscribes a listener from it's event
	 * @param uuid The UUID of the event listener (returned when the listener is created)
	 */
	public void removeEventListener(UUID uuid) {
		if (!eventListenersByUUID.containsKey(uuid)) return;
		var listener = eventListenersByUUID.get(uuid);
		eventListeners.get(listener.getValue0()).remove(listener.getValue1());
		eventListenersByUUID.remove(uuid);
	}
}
