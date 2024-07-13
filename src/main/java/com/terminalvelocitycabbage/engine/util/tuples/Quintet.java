package com.terminalvelocitycabbage.engine.util.tuples;

public class Quintet<A, B, C, D, E> extends Quartet<A, B, C, D> {

    private final E value4;

    public Quintet(A value0, B value1, C value2, D value3, E value4) {
        super(value0, value1, value2, value3);
        this.value4 = value4;
    }

    public Quintet(Unit<A> tuple, B value1, C value2, D value3, E value4) {
        this(tuple.getValue0(), value1, value2, value3, value4);
    }

    public Quintet(Pair<A, B> tuple, C value2, D value3, E value4) {
        this(tuple.getValue0(), tuple.getValue1(), value2, value3, value4);
    }

    public Quintet(Triplet<A, B, C> tuple, D value3, E value4) {
        this(tuple.getValue0(), tuple.getValue1(), tuple.getValue2(), value3, value4);
    }

    public Quintet(Quartet<A, B, C, D> tuple, E value4) {
        this(tuple.getValue0(), tuple.getValue1(), tuple.getValue2(), tuple.getValue3(), value4);
    }

    public E getValue4() {
        return value4;
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
