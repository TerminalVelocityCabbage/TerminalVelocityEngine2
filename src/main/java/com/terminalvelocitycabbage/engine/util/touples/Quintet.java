package com.terminalvelocitycabbage.engine.util.touples;

public class Quintet<A, B, C, D, E> extends Quartet<A, B, C, D> {

    private final E value4;

    public Quintet(A value0, B value1, C value2, D value3, E value4) {
        super(value0, value1, value2, value3);
        this.value4 = value4;
    }

    public E getValue4() {
        return value4;
    }
}
