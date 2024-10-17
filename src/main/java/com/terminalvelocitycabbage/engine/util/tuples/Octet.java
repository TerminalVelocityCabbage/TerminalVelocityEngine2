package com.terminalvelocitycabbage.engine.util.tuples;

public class Octet<A, B, C, D, E, F, G, H> extends Septet<A, B, C, D, E, F, G> {

    private final H value7;

    public Octet(A value0, B value1, C value2, D value3, E value4, F value5, G value6, H value7) {
        super(value0, value1, value2, value3, value4, value5, value6);
        this.value7 = value7;
    }

    public Octet(Unit<A> tuple, B value1, C value2, D value3, E value4, F value5, G value6, H value7) {
        this(tuple.getValue0(), value1, value2, value3, value4, value5, value6, value7);
    }

    public Octet(Pair<A, B> tuple, C value2, D value3, E value4, F value5, G value6, H value7) {
        this(tuple.getValue0(), tuple.getValue1(), value2, value3, value4, value5, value6, value7);
    }

    public Octet(Triplet<A, B, C> tuple, D value3, E value4, F value5, G value6, H value7) {
        this(tuple.getValue0(), tuple.getValue1(), tuple.getValue2(), value3, value4, value5, value6, value7);
    }

    public Octet(Quartet<A, B, C, D> tuple, E value4, F value5, G value6, H value7) {
        this(tuple.getValue0(), tuple.getValue1(), tuple.getValue2(), tuple.getValue3(), value4, value5, value6, value7);
    }

    public Octet(Quintet<A, B, C, D, E> tuple, F value5, G value6, H value7) {
        this(tuple.getValue0(), tuple.getValue1(), tuple.getValue2(), tuple.getValue3(), tuple.getValue4(), value5, value6, value7);
    }

    public Octet(Sextet<A, B, C, D, E, F> tuple, G value6, H value7) {
        this(tuple.getValue0(), tuple.getValue1(), tuple.getValue2(), tuple.getValue3(), tuple.getValue4(), tuple.getValue5(), value6, value7);
    }

    public Octet(Septet<A, B, C, D, E, F, G> tuple, H value7) {
        this(tuple.getValue0(), tuple.getValue1(), tuple.getValue2(), tuple.getValue3(), tuple.getValue4(), tuple.getValue5(), tuple.getValue6(), value7);
    }

    public H getValue7() {
        return value7;
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
