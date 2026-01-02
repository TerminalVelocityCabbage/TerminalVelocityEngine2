package com.terminalvelocitycabbage.engine.scripting.parser.data.intermediate;

import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.scripting.api.ScriptType;

public final class IRPrinter {

    public static String print(IRBlock block) {
        StringBuilder sb = new StringBuilder();
        printBlock(block, sb, 0);
        return sb.toString();
    }

    private static void printBlock(IRBlock block, StringBuilder sb, int indent) {
        indent(sb, indent);
        sb.append("BLOCK ")
                .append(block.kind())
                .append(" ")
                .append(block.identifier())
                .append("\n");

        for (IRNode node : block.body()) {
            if (node instanceof IRAction action) {
                printAction(action, sb, indent + 1);
            }
        }
    }

    private static void printAction(IRAction action, StringBuilder sb, int indent) {
        indent(sb, indent);
        sb.append("ACTION ")
                .append(action.actionId())
                .append(" -> ")
                .append(action.returnType().getIdentifier())
                .append("\n");

        for (IRArgument arg : action.arguments()) {
            indent(sb, indent + 1);
            sb.append("ARG ")
                    .append(arg.name())
                    .append(" = ");
            printValue(arg.value(), sb);
            sb.append("\n");
        }
    }

    private static void printValue(IRValue value, StringBuilder sb) {
        switch (value) {
            case IRLiteral(ScriptType type, Object value1) -> sb
                    .append("LITERAL<")
                    .append(type.getIdentifier())
                    .append(">(")
                    .append(value1)
                    .append(")");
            case IRProperty(ScriptType type, Identifier propertyId, String accessPath) -> sb
                    .append("PROPERTY<")
                    .append(type.getIdentifier())
                    .append(">(")
                    .append(accessPath)
                    .append(")");
            case IREventValue(ScriptType type, String name) -> sb
                    .append("EVENT<")
                    .append(type.getIdentifier())
                    .append(">(")
                    .append(name)
                    .append(")");
            case IRVariable(ScriptType type, String name) -> sb
                    .append("VARIABLE<")
                    .append(type.getIdentifier())
                    .append(">(")
                    .append(name)
                    .append(")");
            case null, default -> {
            }
        }
    }

    private static void indent(StringBuilder sb, int level) {
        sb.append("    ".repeat(level));
    }

}

