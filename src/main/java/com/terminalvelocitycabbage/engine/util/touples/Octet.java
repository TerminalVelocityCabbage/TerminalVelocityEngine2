package com.terminalvelocitycabbage.engine.util.touples;

public class Octet<A, B, C, D, E, F, G, H> extends Septet<A, B, C, D, E, F, G> {

    private H value7;

    public Octet(A value0, B value1, C value2, D value3, E value4, F value5, G value6, H value7) {
        super(value0, value1, value2, value3, value4, value5, value6);
        this.value7 = value7;
    }

    public H getValue7() {
        return value7;
    }

    public void setValue7(H value7) {
        this.value7 = value7;
    }

    @Override
    public String toString() {
        return "Octet{" +
                "value0=" + getValue0() +
                "value1=" + getValue1() +
                "value2=" + getValue2() +
                "value3=" + getValue3() +
                "value4=" + getValue4() +
                "value5=" + getValue5() +
                "value6=" + getValue6() +
                "value7=" + getValue7() +
                '}';
    }
}
