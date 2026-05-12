package com.terminalvelocitycabbage.editor.registry;

import com.terminalvelocitycabbage.editor.Editor;
import com.terminalvelocitycabbage.engine.event.EventDispatcher;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.templates.events.ConfigureTexturesEvent;

import static com.terminalvelocitycabbage.engine.filesystem.resources.ResourceCategory.TEXTURE;

public class EditorTextures {

    public static final Identifier UI_ATLAS = new Identifier(Editor.ID, "atlas", "ui_atlas");

    public static final Identifier TRANSLATE_ICON = TEXTURE.identifierOf(Editor.ID, "translate_icon");
    public static final Identifier ROTATE_ICON = TEXTURE.identifierOf(Editor.ID, "rotate_icon");
    public static final Identifier SCALE_ICON = TEXTURE.identifierOf(Editor.ID, "scale_icon");
    public static final Identifier CARET_OPEN_ICON = TEXTURE.identifierOf(Editor.ID, "caret_open_icon");
    public static final Identifier CARET_CLOSED_ICON = TEXTURE.identifierOf(Editor.ID, "caret_closed_icon");

    public static final Identifier SMILE_TEXTURE = TEXTURE.identifierOf(Editor.ID, "smile");
    public static final Identifier SAD_TEXTURE = TEXTURE.identifierOf(Editor.ID, "sad");

    public static void init(EventDispatcher eventDispatcher) {

        registerAtlas(eventDispatcher, UI_ATLAS);

        registerTexture(eventDispatcher, TRANSLATE_ICON, UI_ATLAS);
        registerTexture(eventDispatcher, ROTATE_ICON, UI_ATLAS);
        registerTexture(eventDispatcher, SCALE_ICON, UI_ATLAS);
        registerTexture(eventDispatcher, CARET_OPEN_ICON, UI_ATLAS);
        registerTexture(eventDispatcher, CARET_CLOSED_ICON, UI_ATLAS);

        registerStandaloneTexture(eventDispatcher, SMILE_TEXTURE);
        registerStandaloneTexture(eventDispatcher, SAD_TEXTURE);
    }

    private static void registerStandaloneTexture(EventDispatcher eventDispatcher, Identifier textureIdentifier) {
        eventDispatcher.listenToEvent(ConfigureTexturesEvent.EVENT, e -> {
            ConfigureTexturesEvent event = (ConfigureTexturesEvent) e;
            event.addTexture(textureIdentifier);
        });
    }

    private static void registerAtlas(EventDispatcher eventDispatcher, Identifier atlasIdentifier) {
        eventDispatcher.listenToEvent(ConfigureTexturesEvent.EVENT, e -> {
            ConfigureTexturesEvent event = (ConfigureTexturesEvent) e;
            event.registerAtlas(atlasIdentifier);
        });
    }

    private static void registerTexture(EventDispatcher eventDispatcher, Identifier textureIdentifier, Identifier... atlasIdentifiers) {
        eventDispatcher.listenToEvent(ConfigureTexturesEvent.EVENT, e -> {
            ConfigureTexturesEvent event = (ConfigureTexturesEvent) e;
            event.addTexture(textureIdentifier, atlasIdentifiers);
        });
    }

    public static void generateAtlases() {
        Editor.getInstance().getTextureCache().generateAtlas(UI_ATLAS);
    }
}
