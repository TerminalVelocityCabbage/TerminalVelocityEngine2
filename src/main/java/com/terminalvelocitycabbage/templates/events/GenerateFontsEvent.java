package com.terminalvelocitycabbage.templates.events;

import com.terminalvelocitycabbage.engine.TerminalVelocityEngine;
import com.terminalvelocitycabbage.engine.client.ui.Font;
import com.terminalvelocitycabbage.engine.event.Event;
import com.terminalvelocitycabbage.engine.filesystem.GameFileSystem;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceCategory;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.registry.Registry;
import org.lwjgl.system.Configuration;
import org.lwjgl.util.freetype.FreeType;

public class GenerateFontsEvent extends Event {

    public static final Identifier EVENT = TerminalVelocityEngine.identifierOf("GenerateFontsEvent");

    private GameFileSystem fileSystem;
    private Registry<Font> fontRegistry;

    public GenerateFontsEvent(GameFileSystem fileSystem, Registry<Font> fontRegistry) {
        super(EVENT);
        this.fileSystem = fileSystem;
        this.fontRegistry = fontRegistry;
        Configuration.HARFBUZZ_LIBRARY_NAME.set(FreeType.getLibrary());
    }

    public Identifier generateFont(Identifier fontResourceIdentifier, int fontSize) {
        var resource = fileSystem.getResource(ResourceCategory.FONT, fontResourceIdentifier);
        var fontIdentifier = new Identifier(fontResourceIdentifier.getNamespace(), fontResourceIdentifier.getName() + "_" + fontSize);
        fontRegistry.register(fontIdentifier, new Font(resource, fontSize));
        return fontIdentifier;
    }
}
