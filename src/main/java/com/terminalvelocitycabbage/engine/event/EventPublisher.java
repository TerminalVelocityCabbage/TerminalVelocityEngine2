package com.terminalvelocitycabbage.engine.event;

import java.util.HashSet;
import java.util.Set;

/**
 * A class which is capable of publishing events to it's subscribers
 */
public class EventPublisher {

    //All the objects which are listening to events from this publisher
    private final Set<Object> subscribers = new HashSet<>();

    /**
     * Subscribes the specified object to this publisher
     * @param subscriber the object which you want to listen to this publisher
     */
    public void subscribe(Object subscriber) {
        subscribers.add(subscriber);
    }

    /**
     * Unsubscribes the specified object to this publisher
     * @param subscriber the object which you want to no longer listen to this publisher
     */
    public void unsubscribe(Object subscriber) {
        subscribers.remove(subscriber);
    }

    /**
     * Clears all subscriptions of this publisher
     */
    public void clearSubscriptions() {
        subscribers.clear();
    }

    /**
     * @return All the subscribed objects to this publisher
     */
    public Set<Object> getSubscribers() {
        return subscribers;
    }
}
