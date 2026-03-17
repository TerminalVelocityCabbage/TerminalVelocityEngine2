package com.terminalvelocitycabbage.editor.registry;

import com.terminalvelocitycabbage.editor.Editor;
import com.terminalvelocitycabbage.engine.event.EventDispatcher;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.templates.events.FontRegistrationEvent;

import static com.terminalvelocitycabbage.engine.filesystem.resources.ResourceCategory.FONT;

public class EditorFonts {

    public static final Identifier THIN = FONT.identifierOf(Editor.ID, "outfit_thin");
    public static final Identifier EXTRA_LIGHT = FONT.identifierOf(Editor.ID, "outfit_extralight");
    public static final Identifier LIGHT = FONT.identifierOf(Editor.ID, "outfit_light");
    public static final Identifier REGULAR = FONT.identifierOf(Editor.ID, "outfit_regular");
    public static final Identifier MEDIUM = FONT.identifierOf(Editor.ID, "outfit_medium");
    public static final Identifier SEMI_BOLD = FONT.identifierOf(Editor.ID, "outfit_semibold");
    public static final Identifier BOLD = FONT.identifierOf(Editor.ID, "outfit_bold");
    public static final Identifier EXTRA_BOLD = FONT.identifierOf(Editor.ID, "outfit_extrabold");
    public static final Identifier BLACK = FONT.identifierOf(Editor.ID, "outfit_black");

    public static void init(EventDispatcher eventDispatcher) {
        registerFont(eventDispatcher, THIN);
        registerFont(eventDispatcher, EXTRA_LIGHT);
        registerFont(eventDispatcher, LIGHT);
        registerFont(eventDispatcher, REGULAR);
        registerFont(eventDispatcher, MEDIUM);
        registerFont(eventDispatcher, SEMI_BOLD);
        registerFont(eventDispatcher, BOLD);
        registerFont(eventDispatcher, EXTRA_BOLD);
        registerFont(eventDispatcher, BLACK);
    }

    public static void registerFont(EventDispatcher eventDispatcher, Identifier name) {
        eventDispatcher.listenToEvent(FontRegistrationEvent.EVENT, e -> ((FontRegistrationEvent) e).registerFont(name));
    }
}
