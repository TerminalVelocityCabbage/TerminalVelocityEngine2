package com.terminalvelocitycabbage.editor.registry;

import com.terminalvelocitycabbage.engine.event.EventDispatcher;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.templates.events.FontRegistrationEvent;
import com.terminalvelocitycabbage.templates.events.ResourceRegistrationEvent;

import static com.terminalvelocitycabbage.editor.TerminalVelocityEngineEditor.ENGINE_RESOURCE_SOURCE;
import static com.terminalvelocitycabbage.engine.filesystem.resources.ResourceCategory.FONT;

public class EditorFonts {

    public static Identifier THIN;
    public static Identifier EXTRA_LIGHT;
    public static Identifier LIGHT;
    public static Identifier REGULAR;
    public static Identifier MEDIUM;
    public static Identifier SEMI_BOLD;
    public static Identifier BOLD;
    public static Identifier EXTRA_BOLD;
    public static Identifier BLACK;

    public static void init(EventDispatcher eventDispatcher) {
        eventDispatcher.listenToEvent(ResourceRegistrationEvent.getEventNameFromCategory(FONT), e -> {
            ResourceRegistrationEvent event = (ResourceRegistrationEvent) e;
            THIN = event.registerResource(ENGINE_RESOURCE_SOURCE, FONT, "outfit_thin.ttf").getIdentifier();
            EXTRA_LIGHT = event.registerResource(ENGINE_RESOURCE_SOURCE, FONT, "outfit_extralight.ttf").getIdentifier();
            LIGHT = event.registerResource(ENGINE_RESOURCE_SOURCE, FONT, "outfit_light.ttf").getIdentifier();
            REGULAR = event.registerResource(ENGINE_RESOURCE_SOURCE, FONT, "outfit_regular.ttf").getIdentifier();
            MEDIUM = event.registerResource(ENGINE_RESOURCE_SOURCE, FONT, "outfit_medium.ttf").getIdentifier();
            SEMI_BOLD = event.registerResource(ENGINE_RESOURCE_SOURCE, FONT, "outfit_semibold.ttf").getIdentifier();
            BOLD = event.registerResource(ENGINE_RESOURCE_SOURCE, FONT, "outfit_bold.ttf").getIdentifier();
            EXTRA_BOLD = event.registerResource(ENGINE_RESOURCE_SOURCE, FONT, "outfit_extrabold.ttf").getIdentifier();
            BLACK = event.registerResource(ENGINE_RESOURCE_SOURCE, FONT, "outfit_black.ttf").getIdentifier();
        });
        eventDispatcher.listenToEvent(FontRegistrationEvent.EVENT, e -> {
            FontRegistrationEvent event = (FontRegistrationEvent) e;
            event.registerFont(THIN);
            event.registerFont(EXTRA_LIGHT);
            event.registerFont(LIGHT);
            event.registerFont(REGULAR);
            event.registerFont(MEDIUM);
            event.registerFont(SEMI_BOLD);
            event.registerFont(BOLD);
            event.registerFont(EXTRA_BOLD);
            event.registerFont(BLACK);
        });
    }
}
