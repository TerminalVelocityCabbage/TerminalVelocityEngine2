package com.terminalvelocitycabbage.engine.client.renderer.animation;

import com.terminalvelocitycabbage.engine.util.Easing;
import org.joml.Vector3f;

/**
 * Represents a portion of a transformation between two target transformations of a single component.
 */
public class Keyframe {

    Component component;
    Vector3f startTransformation;
    Vector3f endTransformation;
    Easing.Function easingFunction;
    float startTime;
    float endTime;

    public Keyframe(Component component, Vector3f startTransformation, Vector3f endTransformation, Easing.Function easingFunction, float startTime, float endTime) {
        this.component = component;
        this.startTransformation = startTransformation;
        this.endTransformation = endTransformation;
        this.easingFunction = easingFunction;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public enum Component {
        POSITION,
        ROTATION,
        SCALE
    }
}
