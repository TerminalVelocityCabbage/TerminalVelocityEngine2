package com.terminalvelocitycabbage.engine.scripting.parser;

import com.terminalvelocitycabbage.engine.scripting.parser.data.ScriptBlock;
import com.terminalvelocitycabbage.engine.scripting.parser.data.intermediate.IRBlock;

public interface BlockHeaderParser {

    boolean matches(String header);

    IRBlock parse(
            ScriptBlock block,
            ParsingContext context
    );
}

