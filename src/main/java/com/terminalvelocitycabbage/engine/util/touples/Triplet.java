package com.terminalvelocitycabbage.engine.util.touples;

public class Triplet<A, B, C> extends Pair<A, B> {

    private final C value2;

    public Triplet(A value0, B value1, C value2) {
        super(value0, value1);
        this.value2 = value2;
    }

    public C getValue2() {
        return value2;
    }
}
