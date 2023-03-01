package com.terminalvelocitycabbage.engine.event;

import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.util.ClassUtils;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class EventDispatcher {

	private static final Map<String, EventPublisher> publishers = new HashMap<>();

	/**
	 * Adds a publisher to this dispatcher
	 * @param namespace the namespace which this publisher can be identified by
	 * @param publisher the actual publisher
	 */
	public void addPublisher(String namespace, EventPublisher publisher) {
		publishers.put(namespace, publisher);
	}

	/**
	 * removes a publisher from this dispatcher
	 * @param publisherNamespace the namespace of the publisher which you are removing
	 */
	public void removePublisher(String publisherNamespace) {
		publishers.get(publisherNamespace).clearSubscriptions();
		publishers.remove(publisherNamespace);
	}

	/**
	 * Goes through all publishers and publishes this event from them
	 * @param event the event which is being dispatched
	 */
	public void dispatchEvent(Event event) {
		for (Object publisher : publishers.values()) {
			dispatchEventTo(event, publisher);
		}
	}

	/**
	 * @param event the event which is being dispatched
	 * @param subscriber the subscribed method that is being invoked
	 */
	protected void dispatchEventTo(Event event, Object subscriber) {
		Collection<Method> methods = findListenerMethodsInObject(subscriber, event.getId());
		for (Method method : methods) {
			try {
				// Workaround for a JDK bug:
				method.setAccessible(true);

				switch (method.getParameterTypes().length) {
					case 0 -> method.invoke(subscriber);
					case 1 -> method.invoke(subscriber, event);
					case 2 -> method.invoke(subscriber, this, event);
				}
			} catch (Exception e) {
				System.err.println("Could not invoke event handler!");
				e.printStackTrace(System.err);
			}
		}
	}

	/**
	 * @param subscriber the object which may contain methods which are subscirbed to the event
	 * @param eventName the event name to compare to the annotation
	 * @return a collection of matching methods which are subscribed to the event specified
	 */
	protected Collection<Method> findListenerMethodsInObject(Object subscriber, Identifier eventName) {
		return ClassUtils.getAllMethodsInHierarchy(subscriber.getClass()).stream().filter(method -> {
			if (method.isAnnotationPresent(HandleEvent.class)) {
				return method.getAnnotation(HandleEvent.class).eventName().equals(eventName.getResourceName());
			}
			return false;
		}).toList();
	}
}
