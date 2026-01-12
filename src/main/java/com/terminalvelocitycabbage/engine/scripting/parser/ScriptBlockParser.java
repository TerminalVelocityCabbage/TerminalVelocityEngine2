package com.terminalvelocitycabbage.engine.scripting.parser;

import com.terminalvelocitycabbage.engine.scripting.parser.data.ScriptBlock;
import com.terminalvelocitycabbage.engine.scripting.parser.data.ScriptLine;

import java.util.ArrayList;
import java.util.List;

public final class ScriptBlockParser {

    public static List<ScriptBlock> parse(String scriptText) {

        List<ScriptBlock> blocks = new ArrayList<>();
        List<String> lines = scriptText.lines().toList();

        ScriptBlock currentBlock = null;
        List<ScriptLine> currentBody = null;
        int lineNumber = 0;
        String rawLine = "";

        for (int i = 0; i < lines.size(); i++) {

            rawLine = lines.get(i);
            lineNumber = i + 1;

            if (rawLine.isBlank()) continue;

            int indent = countLeadingSpaces(rawLine);
            String trimmed = rawLine.trim();

            if (indent == 0) {
                // New block header
                if (currentBlock != null) {
                    blocks.add(currentBlock);
                }

                currentBody = new ArrayList<>();
                currentBlock = new ScriptBlock(
                        trimmed,
                        currentBody,
                        lineNumber
                );
            } else {
                if (currentBlock == null) {
                    throw new RuntimeException("Indented line without a block header at line " + lineNumber);
                }

                currentBody.add(new ScriptLine(trimmed, lineNumber));
            }
        }

        if (currentBlock != null) {
            blocks.add(currentBlock);
        }

        return blocks;
    }

    private static int countLeadingSpaces(String line) {
        int count = 0;
        while (count < line.length() && line.charAt(count) == ' ') {
            count++;
        }
        return count;
    }
}

