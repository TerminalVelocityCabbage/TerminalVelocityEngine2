package com.terminalvelocitycabbage.engine.util.touples;

public class Decade<A, B, C, D, E, F, G, H, I, J> extends Ennead<A, B, C, D, E, F, G, H, I> {

    private final J value9;

    public Decade(A value0, B value1, C value2, D value3, E value4, F value5, G value6, H value7, I value8, J value9) {
        super(value0, value1, value2, value3, value4, value5, value6, value7, value8);
        this.value9 = value9;
    }

    public J getValue9() {
        return value9;
    }
}
