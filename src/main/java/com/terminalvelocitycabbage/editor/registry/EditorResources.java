package com.terminalvelocitycabbage.editor.registry;

import com.terminalvelocitycabbage.editor.Editor;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceCategory;
import com.terminalvelocitycabbage.templates.events.ResourceCategoryRegistrationEvent;

public class EditorResources {

    public static void registerResourceCategories(ResourceCategoryRegistrationEvent event) {
        ResourceCategory.registerEngineDefaults(event.getRegistry(), Editor.ID);
    }

}
