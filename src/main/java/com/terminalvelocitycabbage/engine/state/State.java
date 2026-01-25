package com.terminalvelocitycabbage.engine.state;

public class State<T> {
    T value;

    public State(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
