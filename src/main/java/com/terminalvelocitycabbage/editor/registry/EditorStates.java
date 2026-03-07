package com.terminalvelocitycabbage.editor.registry;

import com.terminalvelocitycabbage.editor.Editor;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.event.EventDispatcher;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.templates.events.GameStateRegistrationEvent;

import java.nio.file.Path;

public class EditorStates {

    public static Identifier ASSET_LOCATION;

    public static void init(EventDispatcher eventDispatcher) {

        eventDispatcher.listenToEvent(GameStateRegistrationEvent.EVENT, e -> {
            GameStateRegistrationEvent event = (GameStateRegistrationEvent) e;

            ASSET_LOCATION = event.registerState(Editor.getInstance().getNamespace(), "asset_location", Path.of("unset"));
        });
    }

    public static Path getAssetLocation() {
        return (Path) Editor.getInstance().getStateHandler().getState(ASSET_LOCATION).getValue();
    }

    public static void setAssetLocation(Path location) {
        Editor.getInstance().getStateHandler().getState(ASSET_LOCATION).setValue(location);
        Log.info("Asset location set to: " + location);
    }

}
