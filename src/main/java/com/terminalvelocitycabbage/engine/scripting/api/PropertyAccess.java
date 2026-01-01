package com.terminalvelocitycabbage.engine.scripting.api;

import java.util.function.BiConsumer;
import java.util.function.Function;

public sealed interface PropertyAccess<T> permits PropertyAccess.ReadOnly, PropertyAccess.ReadWrite {

    Object get(T owner);

    record ReadOnly<T>(Function<T, Object> getter) implements PropertyAccess<T> {

        @Override
        public Object get(T owner) {
            return getter.apply(owner);
        }
    }

    record ReadWrite<T>(Function<T, Object> getter, BiConsumer<T, Object> setter) implements PropertyAccess<T> {

        @Override
        public Object get(T owner) {
            return getter.apply(owner);
        }
    }
}

