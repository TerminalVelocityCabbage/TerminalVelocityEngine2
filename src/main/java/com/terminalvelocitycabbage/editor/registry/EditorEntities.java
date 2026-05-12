package com.terminalvelocitycabbage.editor.registry;

import com.terminalvelocitycabbage.editor.ecs.components.EditorCameraComponent;
import com.terminalvelocitycabbage.templates.ecs.components.CameraComponent;
import com.terminalvelocitycabbage.templates.ecs.components.DirectionalLightComponent;
import com.terminalvelocitycabbage.templates.ecs.components.NameComponent;
import com.terminalvelocitycabbage.templates.events.EntityComponentRegistrationEvent;
import com.terminalvelocitycabbage.templates.events.EntitySystemRegistrationEvent;

public class EditorEntities {

    public static void registerComponents(EntityComponentRegistrationEvent event) {
        event.registerComponent(NameComponent.class);
        event.registerComponent(CameraComponent.class);
        event.registerComponent(EditorCameraComponent.class);
        event.registerComponent(DirectionalLightComponent.class);
    }

    public static void createSystems(EntitySystemRegistrationEvent event) {

    }

}
