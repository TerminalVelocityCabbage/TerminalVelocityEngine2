package com.terminalvelocitycabbage.engine.scripting.parser.data;

import java.util.List;

public final class ScriptFile {

    private final List<ScriptBlock> blocks;

    public ScriptFile(List<ScriptBlock> blocks) {
        this.blocks = blocks;
    }
}
