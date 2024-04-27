package com.terminalvelocitycabbage.engine.util.touples;

public class Pair<A, B> extends Unit<A> {

    private final B value1;

    public Pair(A value0, B value1) {
        super(value0);
        this.value1 = value1;
    }

    public B getValue1() {
        return value1;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "value0=" + getValue0() +
                "value1=" + getValue1() +
                '}';
    }
}
