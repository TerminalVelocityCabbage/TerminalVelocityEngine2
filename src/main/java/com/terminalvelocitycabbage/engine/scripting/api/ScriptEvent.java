package com.terminalvelocitycabbage.engine.scripting.api;

import com.terminalvelocitycabbage.engine.registry.Identifiable;
import com.terminalvelocitycabbage.engine.registry.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public record ScriptEvent<E>(
        Identifier identifier,
        Class<?> eventClass,
        Map<String, ScriptEventValue<E, ?>> exposedValues,
        String documentation) implements Identifiable {

    public static <E> ScriptEventBuilder<E> builder() {
        return new ScriptEventBuilder<>();
    }

    public static final class ScriptEventBuilder<E> {

        private Identifier id;
        private Class<E> eventClass;
        private final Map<String, ScriptEventValue> values = new LinkedHashMap<>();
        private String documentation = "";

        public ScriptEventBuilder<E> id(Identifier id) {
            this.id = id;
            return this;
        }

        public ScriptEventBuilder<E> eventClass(Class<E> eventClass) {
            this.eventClass = eventClass;
            return this;
        }

        public <T> ScriptEventBuilder<E> exposedValue(
                String name,
                ScriptType type,
                Function<E, T> extractor
        ) {
            values.put(
                    name,
                    new ScriptEventValue(
                            name,
                            type,
                            (Function<Object, Object>) extractor
                    )
            );
            return this;
        }

        public ScriptEventBuilder<E> doc(String documentation) {
            this.documentation = documentation;
            return this;
        }

        public ScriptEvent build() {
            if (id == null)
                throw new IllegalStateException("ScriptEvent identifier is required");
            if (eventClass == null)
                throw new IllegalStateException("ScriptEvent eventClass is required");

            return new ScriptEvent(
                    id,
                    eventClass,
                    Map.copyOf(values),
                    documentation
            );
        }
    }


    @Override
    public Identifier getIdentifier() {
        return identifier;
    }
}
