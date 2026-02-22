package com.terminalvelocitycabbage.editor.registry;

import com.terminalvelocitycabbage.editor.TerminalVelocityEngineEditor;
import com.terminalvelocitycabbage.engine.event.EventDispatcher;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceCategory;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.templates.events.ConfigureTexturesEvent;
import com.terminalvelocitycabbage.templates.events.ResourceRegistrationEvent;

public class EditorTextures {

    public static Identifier UI_ATLAS;

    public static Identifier TRANSLATE_ICON;
    public static Identifier ROTATE_ICON;
    public static Identifier SCALE_ICON;

    public static void init(EventDispatcher eventDispatcher) {

        eventDispatcher.listenToEvent(ResourceRegistrationEvent.getEventNameFromCategory(ResourceCategory.TEXTURE), e -> {
            ResourceRegistrationEvent event = (ResourceRegistrationEvent) e;

            TRANSLATE_ICON = registerTexture(eventDispatcher, event, "translate_icon.png");
            ROTATE_ICON = registerTexture(eventDispatcher, event, "rotate_icon.png");
            SCALE_ICON = registerTexture(eventDispatcher, event, "scale_icon.png");
        });
    }

    private static Identifier registerTexture(EventDispatcher eventDispatcher, ResourceRegistrationEvent event, String textureName) {
        Identifier textureIdentifier = event.registerResource(TerminalVelocityEngineEditor.ENGINE_RESOURCE_SOURCE, ResourceCategory.TEXTURE, textureName).getIdentifier();
        eventDispatcher.listenToEvent(ConfigureTexturesEvent.EVENT, e -> {
            ConfigureTexturesEvent configureEvent = (ConfigureTexturesEvent) e;

            UI_ATLAS = configureEvent.registerAtlas(TerminalVelocityEngineEditor.ID, "ui_atlas");
            configureEvent.addTexture(textureIdentifier, UI_ATLAS);
        });
        return textureIdentifier;
    }

    public static void generateAtlases() {
        TerminalVelocityEngineEditor.getInstance().getTextureCache().generateAtlas(UI_ATLAS);
    }
}
