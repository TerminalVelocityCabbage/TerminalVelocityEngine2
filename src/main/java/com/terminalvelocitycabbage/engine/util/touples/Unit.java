package com.terminalvelocitycabbage.engine.util.touples;

public class Unit<A> {

    private final A value0;

    public Unit(A value0) {
        this.value0 = value0;
    }

    public A getValue0() {
        return value0;
    }

    @Override
    public String toString() {
        return "Unit{" +
                "value0=" + getValue0() +
                '}';
    }

}
