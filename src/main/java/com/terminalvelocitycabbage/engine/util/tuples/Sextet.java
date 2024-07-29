package com.terminalvelocitycabbage.engine.util.tuples;

public class Sextet<A, B, C, D, E, F> extends Quintet<A, B, C, D, E> {

    private final F value5;

    public Sextet(A value0, B value1, C value2, D value3, E value4, F value5) {
        super(value0, value1, value2, value3, value4);
        this.value5 = value5;
    }

    public Sextet(Unit<A> tuple, B value1, C value2, D value3, E value4, F value5) {
        this(tuple.getValue0(), value1, value2, value3, value4, value5);
    }

    public Sextet(Pair<A, B> tuple, C value2, D value3, E value4, F value5) {
        this(tuple.getValue0(), tuple.getValue1(), value2, value3, value4, value5);
    }

    public Sextet(Triplet<A, B, C> tuple, D value3, E value4, F value5) {
        this(tuple.getValue0(), tuple.getValue1(), tuple.getValue2(), value3, value4, value5);
    }

    public Sextet(Quartet<A, B, C, D> tuple, E value4, F value5) {
        this(tuple.getValue0(), tuple.getValue1(), tuple.getValue2(), tuple.getValue3(), value4, value5);
    }

    public Sextet(Quintet<A, B, C, D, E> tuple, F value5) {
        this(tuple.getValue0(), tuple.getValue1(), tuple.getValue2(), tuple.getValue3(), tuple.getValue4(), value5);
    }

    public F getValue5() {
        return value5;
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
