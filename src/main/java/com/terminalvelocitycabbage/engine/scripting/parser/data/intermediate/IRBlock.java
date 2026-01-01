package com.terminalvelocitycabbage.engine.scripting.parser.data.intermediate;

import com.terminalvelocitycabbage.engine.registry.Identifier;

import java.util.List;

public record IRBlock(BlockKind kind, Identifier identifier, List<IRNode> body) implements IRNode {
}
