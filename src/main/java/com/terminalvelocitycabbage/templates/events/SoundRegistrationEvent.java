package com.terminalvelocitycabbage.templates.events;

import com.terminalvelocitycabbage.engine.TerminalVelocityEngine;
import com.terminalvelocitycabbage.engine.client.sound.SoundData;
import com.terminalvelocitycabbage.engine.client.sound.SoundManager;
import com.terminalvelocitycabbage.engine.event.Event;
import com.terminalvelocitycabbage.engine.registry.Identifier;

public class SoundRegistrationEvent extends Event {

    public static final Identifier EVENT = TerminalVelocityEngine.identifierOf("event", "register_sounds");

    SoundManager soundManager;

    public SoundRegistrationEvent(SoundManager soundManager) {
        super(EVENT);
        this.soundManager = soundManager;
    }

    public Identifier registerSound(Identifier soundResource) {
        var soundData = new SoundData(soundResource);
        soundManager.addSound(soundData);
        return soundData.getIdentifier();
    }

}
