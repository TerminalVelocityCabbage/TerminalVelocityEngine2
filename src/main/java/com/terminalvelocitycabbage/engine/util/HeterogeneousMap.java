package com.terminalvelocitycabbage.engine.util;

import java.util.HashMap;
import java.util.Map;

public class HeterogeneousMap {

    private final Map<Key<?>, Object> values;

    public HeterogeneousMap() {
        this.values = new HashMap<>();
    }

    public <T> void set(Key<T> key, T value) {
        values.put(key, value);
    }

    public <T> T get(Key<T> key) {
        Object value = values.get(key);
        if (value == null) return null;
        return key.type().cast(value);  // Safe cast
    }

    public void addAll(HeterogeneousMap renderConfig) {
        values.putAll(renderConfig.values);
    }

    public record Key<T>(String name, Class<T> type) {

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Key<?> other)) return false;
            return name.equals(other.name);
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }
    }

}
