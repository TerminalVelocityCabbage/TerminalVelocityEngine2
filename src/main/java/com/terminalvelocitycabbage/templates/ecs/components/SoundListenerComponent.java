package com.terminalvelocitycabbage.templates.ecs.components;

import com.terminalvelocitycabbage.engine.client.ClientBase;
import com.terminalvelocitycabbage.engine.client.sound.SoundListener;
import com.terminalvelocitycabbage.engine.ecs.Component;

public class SoundListenerComponent implements Component {

    SoundListener listener;

    public SoundListenerComponent() {
        this.listener = new SoundListener();
        ClientBase.getInstance().getSoundManager().setListener(listener);
    }

    @Override
    public void setDefaults() {

    }

    @Override
    public void cleanup() {
        Component.super.cleanup();
    }

    public void update(TransformationComponent transformationComponent, VelocityComponent velocityComponent, CameraComponent cameraComponent) {
        var transformation = transformationComponent.getTransformation();
        listener.setAll(transformation, velocityComponent.getVelocity(), cameraComponent.getViewMatrix(transformation));
    }
}
