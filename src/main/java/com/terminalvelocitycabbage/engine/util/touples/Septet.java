package com.terminalvelocitycabbage.engine.util.touples;

public class Septet<A, B, C, D, E, F, G> extends Sextet<A, B, C, D, E, F> {

    private final G value6;

    public Septet(A value0, B value1, C value2, D value3, E value4, F value5, G value6) {
        super(value0, value1, value2, value3, value4, value5);
        this.value6 = value6;
    }

    public G getValue6() {
        return value6;
    }
}
