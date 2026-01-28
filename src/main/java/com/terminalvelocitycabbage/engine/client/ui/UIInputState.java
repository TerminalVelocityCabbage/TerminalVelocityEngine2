package com.terminalvelocitycabbage.engine.client.ui;

import com.terminalvelocitycabbage.engine.event.Event;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class UIInputState {

    private final Vector2f mousePosition = new Vector2f();
    private final List<Event> events = new ArrayList<>();

    public UIInputState() {
    }

    public void copyFrom(UIInputState other) {
        synchronized (other) {
            this.mousePosition.set(other.mousePosition);
            this.events.clear();
            this.events.addAll(other.events);
        }
    }

    public void resetOneTimeState() {
        events.clear();
    }

    public Vector2f getMousePosition() {
        return mousePosition;
    }

    public List<Event> getEvents() {
        return events;
    }
}
