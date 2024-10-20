package com.terminalvelocitycabbage.engine.util.touples;

public class Sextet<A, B, C, D, E, F> extends Quintet<A, B, C, D, E> {

    private F value5;

    public Sextet(A value0, B value1, C value2, D value3, E value4, F value5) {
        super(value0, value1, value2, value3, value4);
        this.value5 = value5;
    }

    public F getValue5() {
        return value5;
    }

    public void setValue5(F value5) {
        this.value5 = value5;
    }

    @Override
    public String toString() {
        return "Sextet{" +
                "value0=" + getValue0() +
                "value1=" + getValue1() +
                "value2=" + getValue2() +
                "value3=" + getValue3() +
                "value4=" + getValue4() +
                "value5=" + getValue5() +
                '}';
    }
}
