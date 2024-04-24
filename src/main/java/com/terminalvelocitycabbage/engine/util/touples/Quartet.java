package com.terminalvelocitycabbage.engine.util.touples;

public class Quartet<A, B, C, D> extends Triplet<A, B, C> {

    private final D value3;

    public Quartet(A value0, B value1, C value2, D value3) {
        super(value0, value1, value2);
        this.value3 = value3;
    }

    public D getValue3() {
        return value3;
    }
}
