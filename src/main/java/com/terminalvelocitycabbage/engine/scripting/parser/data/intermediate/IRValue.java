package com.terminalvelocitycabbage.engine.scripting.parser.data.intermediate;

import com.terminalvelocitycabbage.engine.scripting.api.ScriptType;

public sealed interface IRValue permits IRLiteral, IRProperty, IREventValue, IRVariable {

    ScriptType type();
}

