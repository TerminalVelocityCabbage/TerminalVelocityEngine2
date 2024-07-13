package com.terminalvelocitycabbage.engine.util.tuples;

public class Ennead<A, B, C, D, E, F, G, H, I> extends Octet<A, B, C, D, E, F, G, H> {

    private final I value8;

    public Ennead(A value0, B value1, C value2, D value3, E value4, F value5, G value6, H value7, I value8) {
        super(value0, value1, value2, value3, value4, value5, value6, value7);
        this.value8 = value8;
    }

    public Ennead(Unit<A> tuple, B value1, C value2, D value3, E value4, F value5, G value6, H value7, I value8) {
        this(tuple.getValue0(), value1, value2, value3, value4, value5, value6, value7, value8);
    }

    public Ennead(Pair<A, B> tuple, C value2, D value3, E value4, F value5, G value6, H value7, I value8) {
        this(tuple.getValue0(), tuple.getValue1(), value2, value3, value4, value5, value6, value7, value8);
    }

    public Ennead(Triplet<A, B, C> tuple, D value3, E value4, F value5, G value6, H value7, I value8) {
        this(tuple.getValue0(), tuple.getValue1(), tuple.getValue2(), value3, value4, value5, value6, value7, value8);
    }

    public Ennead(Quartet<A, B, C, D> tuple, E value4, F value5, G value6, H value7, I value8) {
        this(tuple.getValue0(), tuple.getValue1(), tuple.getValue2(), tuple.getValue3(), value4, value5, value6, value7, value8);
    }

    public Ennead(Quintet<A, B, C, D, E> tuple, F value5, G value6, H value7, I value8) {
        this(tuple.getValue0(), tuple.getValue1(), tuple.getValue2(), tuple.getValue3(), tuple.getValue4(), value5, value6, value7, value8);
    }

    public Ennead(Sextet<A, B, C, D, E, F> tuple, G value6, H value7, I value8) {
        this(tuple.getValue0(), tuple.getValue1(), tuple.getValue2(), tuple.getValue3(), tuple.getValue4(), tuple.getValue5(), value6, value7, value8);
    }

    public Ennead(Septet<A, B, C, D, E, F, G> tuple, H value7, I value8) {
        this(tuple.getValue0(), tuple.getValue1(), tuple.getValue2(), tuple.getValue3(), tuple.getValue4(), tuple.getValue5(), tuple.getValue6(), value7, value8);
    }

    public Ennead(Octet<A, B, C, D, E, F, G, H> tuple, I value8) {
        this(tuple.getValue0(), tuple.getValue1(), tuple.getValue2(), tuple.getValue3(), tuple.getValue4(), tuple.getValue5(), tuple.getValue6(), tuple.getValue7(), value8);
    }

    public I getValue8() {
        return value8;
    }

    @Override
    public String toString() {
        return "Ennead{" +
                "value0=" + getValue0() +
                "value1=" + getValue1() +
                "value2=" + getValue2() +
                "value3=" + getValue3() +
                "value4=" + getValue4() +
                "value5=" + getValue5() +
                "value6=" + getValue6() +
                "value7=" + getValue7() +
                "value8=" + getValue8() +
                '}';
    }
}
