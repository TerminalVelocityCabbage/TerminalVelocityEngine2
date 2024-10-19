package com.terminalvelocitycabbage.engine.util.touples;

public class Quintet<A, B, C, D, E> extends Quartet<A, B, C, D> {

    private E value4;

    public Quintet(A value0, B value1, C value2, D value3, E value4) {
        super(value0, value1, value2, value3);
        this.value4 = value4;
    }

    public E getValue4() {
        return value4;
    }

    public void setValue4(E value4) {
        this.value4 = value4;
    }

    @Override
    public String toString() {
        return "Quintet{" +
                "value0=" + getValue0() +
                "value1=" + getValue1() +
                "value2=" + getValue2() +
                "value3=" + getValue3() +
                "value4=" + getValue4() +
                '}';
    }
}
