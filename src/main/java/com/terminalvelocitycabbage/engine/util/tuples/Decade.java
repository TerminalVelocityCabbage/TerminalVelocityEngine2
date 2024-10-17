package com.terminalvelocitycabbage.engine.util.tuples;

public class Decade<A, B, C, D, E, F, G, H, I, J> extends Ennead<A, B, C, D, E, F, G, H, I> {

    private final J value9;

    public Decade(A value0, B value1, C value2, D value3, E value4, F value5, G value6, H value7, I value8, J value9) {
        super(value0, value1, value2, value3, value4, value5, value6, value7, value8);
        this.value9 = value9;
    }

    public Decade(Unit<A> tuple, B value1, C value2, D value3, E value4, F value5, G value6, H value7, I value8, J value9) {
        this(tuple.getValue0(), value1, value2, value3, value4, value5, value6, value7, value8, value9);
    }

    public Decade(Pair<A, B> tuple, C value2, D value3, E value4, F value5, G value6, H value7, I value8, J value9) {
        this(tuple.getValue0(), tuple.getValue1(), value2, value3, value4, value5, value6, value7, value8, value9);
    }

    public Decade(Triplet<A, B, C> tuple, D value3, E value4, F value5, G value6, H value7, I value8, J value9) {
        this(tuple.getValue0(), tuple.getValue1(), tuple.getValue2(), value3, value4, value5, value6, value7, value8, value9);
    }

    public Decade(Quartet<A, B, C, D> tuple, E value4, F value5, G value6, H value7, I value8, J value9) {
        this(tuple.getValue0(), tuple.getValue1(), tuple.getValue2(), tuple.getValue3(), value4, value5, value6, value7, value8, value9);
    }

    public Decade(Quintet<A, B, C, D, E> tuple, F value5, G value6, H value7, I value8, J value9) {
        this(tuple.getValue0(), tuple.getValue1(), tuple.getValue2(), tuple.getValue3(), tuple.getValue4(), value5, value6, value7, value8, value9);
    }

    public Decade(Sextet<A, B, C, D, E, F> tuple, G value6, H value7, I value8, J value9) {
        this(tuple.getValue0(), tuple.getValue1(), tuple.getValue2(), tuple.getValue3(), tuple.getValue4(), tuple.getValue5(), value6, value7, value8, value9);
    }

    public Decade(Septet<A, B, C, D, E, F, G> tuple, H value7, I value8, J value9) {
        this(tuple.getValue0(), tuple.getValue1(), tuple.getValue2(), tuple.getValue3(), tuple.getValue4(), tuple.getValue5(), tuple.getValue6(), value7, value8, value9);
    }

    public Decade(Octet<A, B, C, D, E, F, G, H> tuple, I value8, J value9) {
        this(tuple.getValue0(), tuple.getValue1(), tuple.getValue2(), tuple.getValue3(), tuple.getValue4(), tuple.getValue5(), tuple.getValue6(), tuple.getValue7(), value8, value9);
    }

    public Decade(Ennead<A, B, C, D, E, F, G, H, I> tuple, J value9) {
        this(tuple.getValue0(), tuple.getValue1(), tuple.getValue2(), tuple.getValue3(), tuple.getValue4(), tuple.getValue5(), tuple.getValue6(), tuple.getValue7(), tuple.getValue8(), value9);
    }

    public J getValue9() {
        return value9;
    }

    @Override
    public String toString() {
        return "Decade{" +
                "value0=" + getValue0() +
                "value1=" + getValue1() +
                "value2=" + getValue2() +
                "value3=" + getValue3() +
                "value4=" + getValue4() +
                "value5=" + getValue5() +
                "value6=" + getValue6() +
                "value7=" + getValue7() +
                "value8=" + getValue8() +
                "value9=" + getValue9() +
                '}';
    }
}
