package com.terminalvelocitycabbage.templates.events;

import com.terminalvelocitycabbage.engine.TerminalVelocityEngine;
import com.terminalvelocitycabbage.engine.event.Event;
import com.terminalvelocitycabbage.engine.filesystem.GameFileSystem;
import com.terminalvelocitycabbage.engine.registry.Identifier;

public class LoadBedrockAnimationsEvent extends Event {

    public static final Identifier EVENT = TerminalVelocityEngine.identifierOf("LoadBedrockAnimationsEvent");

    private final GameFileSystem fileSystem;

    public LoadBedrockAnimationsEvent(GameFileSystem fileSystem) {
        super(EVENT);
        this.fileSystem = fileSystem;
    }

    public GameFileSystem getFileSystem() {
        return fileSystem;
    }
}
