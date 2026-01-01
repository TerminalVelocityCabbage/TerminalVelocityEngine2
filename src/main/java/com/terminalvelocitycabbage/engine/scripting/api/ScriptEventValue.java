package com.terminalvelocitycabbage.engine.scripting.api;

import java.util.function.Function;

public record ScriptEventValue(
        String name,
        ScriptType type,
        Function<Object, Object> extractor
) {}

