package com.terminalvelocitycabbage.engine.scripting.parser.data.intermediate;

import com.terminalvelocitycabbage.engine.scripting.api.ScriptType;

public sealed interface IRValue permits IREventValue, IRLiteralValue, IRPropertyValue, IRVariable {

    ScriptType type();
}

