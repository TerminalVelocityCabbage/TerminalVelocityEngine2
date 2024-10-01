package com.terminalvelocitycabbage.engine.util.tuples;

public class Septet<A, B, C, D, E, F, G> extends Sextet<A, B, C, D, E, F> {

    private final G value6;

    public Septet(A value0, B value1, C value2, D value3, E value4, F value5, G value6) {
        super(value0, value1, value2, value3, value4, value5);
        this.value6 = value6;
    }

    public Septet(Unit<A> tuple, B value1, C value2, D value3, E value4, F value5, G value6) {
        this(tuple.getValue0(), value1, value2, value3, value4, value5, value6);
    }

    public Septet(Pair<A, B> tuple, C value2, D value3, E value4, F value5, G value6) {
        this(tuple.getValue0(), tuple.getValue1(), value2, value3, value4, value5, value6);
    }

    public Septet(Triplet<A, B, C> tuple, D value3, E value4, F value5, G value6) {
        this(tuple.getValue0(), tuple.getValue1(), tuple.getValue2(), value3, value4, value5, value6);
    }

    public Septet(Quartet<A, B, C, D> tuple, E value4, F value5, G value6) {
        this(tuple.getValue0(), tuple.getValue1(), tuple.getValue2(), tuple.getValue3(), value4, value5, value6);
    }

    public Septet(Quintet<A, B, C, D, E> tuple, F value5, G value6) {
        this(tuple.getValue0(), tuple.getValue1(), tuple.getValue2(), tuple.getValue3(), tuple.getValue4(), value5, value6);
    }

    public Septet(Sextet<A, B, C, D, E, F> tuple, G value6) {
        this(tuple.getValue0(), tuple.getValue1(), tuple.getValue2(), tuple.getValue3(), tuple.getValue4(), tuple.getValue5(), value6);
    }

    public G getValue6() {
        return value6;
    }

    @Override
    public String toString() {
        return "Septet{" +
                "value0=" + getValue0() +
                "value1=" + getValue1() +
                "value2=" + getValue2() +
                "value3=" + getValue3() +
                "value4=" + getValue4() +
                "value5=" + getValue5() +
                "value6=" + getValue6() +
                '}';
    }
}
