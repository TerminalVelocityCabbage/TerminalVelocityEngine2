package com.terminalvelocitycabbage.engine.scripting.parser.core;

import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.scripting.parser.BlockHeaderParser;
import com.terminalvelocitycabbage.engine.scripting.parser.ParsingContext;
import com.terminalvelocitycabbage.engine.scripting.parser.data.ScriptBlock;
import com.terminalvelocitycabbage.engine.scripting.parser.data.intermediate.BlockKind;
import com.terminalvelocitycabbage.engine.scripting.parser.data.intermediate.IRBlock;

import java.util.ArrayList;

public final class EventBlockHeaderParser implements BlockHeaderParser {

    @Override
    public boolean matches(String header) {
        return header.startsWith("on ") || header.startsWith("when ");
    }

    @Override
    public IRBlock parse(ScriptBlock block, ParsingContext context) {

        String header = block.headerLine();

        // Remove trailing colon if present
        if (header.endsWith(":")) {
            header = header.substring(0, header.length() - 1);
        }

        String[] parts = header.split("\\s+", 2);
        if (parts.length != 2) {
            throw new RuntimeException(
                    "Invalid event block header at line "
                            + block.headerLineNumber()
            );
        }

        String eventName = parts[1];
        Identifier eventId;

        if (Identifier.isValidIdentifierString(eventName)) {
            eventId = Identifier.of(eventName);
        } else {

            var events = context.events().getIdentifiersWithName(eventName);

            if (events.size() > 1) {
                throw new RuntimeException(
                        "Ambiguous event name '" + eventName + "' at line " + block.headerLineNumber()
                );
            }

            eventId = events.iterator().next();

            if (!context.events().contains(eventId)) {
                throw new RuntimeException(
                        "Unknown event '" + eventName
                                + "' at line " + block.headerLineNumber()
                );
            }
        }

        return new IRBlock(
                BlockKind.EVENT,
                eventId,
                new ArrayList<>() // body filled later
        );
    }
}

