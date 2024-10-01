package com.terminalvelocitycabbage.engine.util.tuples;

public class Quartet<A, B, C, D> extends Triplet<A, B, C> {

    private final D value3;

    public Quartet(A value0, B value1, C value2, D value3) {
        super(value0, value1, value2);
        this.value3 = value3;
    }

    public Quartet(Unit<A> tuple, B value1, C value2, D value3) {
        this(tuple.getValue0(), value1, value2, value3);
    }

    public Quartet(Pair<A, B> tuple, C value2, D value3) {
        this(tuple.getValue0(), tuple.getValue1(), value2, value3);
    }

    public Quartet(Triplet<A, B, C> tuple, D value3) {
        this(tuple.getValue0(), tuple.getValue1(), tuple.getValue2(), value3);
    }

    public D getValue3() {
        return value3;
    }

    @Override
    public String toString() {
        return "Quartet{" +
                "value0=" + getValue0() +
                "value1=" + getValue1() +
                "value2=" + getValue2() +
                "value3=" + getValue3() +
                '}';
    }
}
