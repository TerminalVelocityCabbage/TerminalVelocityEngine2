package com.terminalvelocitycabbage.templates.ecs.components;

import com.terminalvelocitycabbage.engine.client.ClientBase;
import com.terminalvelocitycabbage.engine.client.sound.SoundSource;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.ecs.Component;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import org.joml.Vector3f;

//TODO add utilities for playing with different pitches etc, not required in MVP
public class SoundSourceComponent implements Component {

    SoundSource source;

    public SoundSourceComponent() {
        this.source = new SoundSource();
        ClientBase.getInstance().getSoundManager().addSoundSource(source);
    }

    @Override
    public void setDefaults() {
        this.source.pause();
    }

    public void update(TransformationComponent transformation, VelocityComponent velocity) {
        source.setPosition(transformation.getPosition());
        source.setVelocity(velocity.getVelocity());
        source.setDirection(transformation.getRotation().getEulerAnglesXYZ(new Vector3f()));
    }

    public void playSound(Identifier sound) {
        source.setSound(sound);
        source.play();
    }

    public void loopSound(Identifier sound) {
        source.setSound(sound);
        source.setLooping(true);
        source.play();
    }

    public void pause() {
        source.pause();
    }

    public void stop() {
        source.stop();
    }

    public boolean isPlaying() {
        return source.isPlaying();
    }
}
