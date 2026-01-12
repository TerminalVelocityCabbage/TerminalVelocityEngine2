package com.terminalvelocitycabbage.engine.scripting.api.registry;

import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.registry.Registry;
import com.terminalvelocitycabbage.engine.scripting.api.ScriptProperty;

import java.util.List;

public class ScriptPropertyRegistry extends Registry<ScriptProperty<?, ?>> {

    public ScriptProperty<?, ?> resolve(String raw) {

        // 1. Fully-qualified identifier
        if (Identifier.isValidIdentifierString(raw)) {
            Identifier id = Identifier.of(raw);

            ScriptProperty<?, ?> property = get(id);
            if (property == null) {
                throw new RuntimeException("Unknown property '" + raw + "'");
            }

            return property;
        }

        // 2. Unqualified name → search by name
        List<ScriptProperty<?, ?>> matches =
                getRegistryContents().values().stream()
                        .filter(p -> p.getIdentifier().getName().equals(raw))
                        .toList();

        if (matches.isEmpty()) {
            throw new RuntimeException("Unknown property '" + raw + "'");
        }

        if (matches.size() > 1) {
            throw new RuntimeException(
                    "Ambiguous property '" + raw + "': " +
                            matches.stream()
                                    .map(p -> p.getIdentifier().toString())
                                    .toList()
            );
        }

        return matches.get(0);
    }

}
