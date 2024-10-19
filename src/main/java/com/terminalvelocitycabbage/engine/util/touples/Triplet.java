package com.terminalvelocitycabbage.engine.util.touples;

public class Triplet<A, B, C> extends Pair<A, B> {

    private C value2;

    public Triplet(A value0, B value1, C value2) {
        super(value0, value1);
        this.value2 = value2;
    }

    public C getValue2() {
        return value2;
    }

    public void setValue2(C value2) {
        this.value2 = value2;
    }

    @Override
    public String toString() {
        return "Triplet{" +
                "value0=" + getValue0() +
                "value1=" + getValue1() +
                "value2=" + getValue2() +
                '}';
    }
}
