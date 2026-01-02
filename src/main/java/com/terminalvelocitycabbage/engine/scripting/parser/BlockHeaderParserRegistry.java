package com.terminalvelocitycabbage.engine.scripting.parser;

import com.terminalvelocitycabbage.engine.registry.Registry;
import com.terminalvelocitycabbage.engine.scripting.parser.data.ScriptBlock;
import com.terminalvelocitycabbage.engine.scripting.parser.data.intermediate.IRBlock;

public final class BlockHeaderParserRegistry extends Registry<BlockHeaderParser> {

    public IRBlock parse(
            ScriptBlock block,
            ParsingContext context
    ) {
        for (BlockHeaderParser parser : registryContents.values()) {
            if (parser.matches(block.headerLine())) {
                return parser.parse(block, context);
            }
        }

        throw new RuntimeException(
                "Unknown block header '"
                        + block.headerLine()
                        + "' at line "
                        + block.headerLineNumber()
        );
    }
}

