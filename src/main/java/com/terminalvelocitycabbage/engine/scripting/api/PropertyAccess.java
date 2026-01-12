package com.terminalvelocitycabbage.engine.scripting.api;

import java.util.function.BiConsumer;
import java.util.function.Function;

public sealed interface PropertyAccess<O, V>
        permits PropertyAccess.ReadOnly, PropertyAccess.ReadWrite {

    V get(O instance);

    final class ReadOnly<O, V> implements PropertyAccess<O, V> {

        private final Function<O, V> getter;

        public ReadOnly(Function<O, V> getter) {
            this.getter = getter;
        }

        @Override
        public V get(O instance) {
            return getter.apply(instance);
        }
    }

    final class ReadWrite<O, V> implements PropertyAccess<O, V> {

        private final Function<O, V> getter;
        private final BiConsumer<O, V> setter;

        public ReadWrite(Function<O, V> getter, BiConsumer<O, V> setter) {
            this.getter = getter;
            this.setter = setter;
        }

        @Override
        public V get(O instance) {
            return getter.apply(instance);
        }

        // setter omitted for now
    }
}


