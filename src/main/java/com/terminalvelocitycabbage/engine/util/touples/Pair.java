package com.terminalvelocitycabbage.engine.util.touples;

public class Pair<A, B> {

    private final A value0;
    private final B value1;

    public Pair(A value0, B value1) {
        this.value0 = value0;
        this.value1 = value1;
    }

    public A getValue0() {
        return value0;
    }

    public B getValue1() {
        return value1;
    }
}
