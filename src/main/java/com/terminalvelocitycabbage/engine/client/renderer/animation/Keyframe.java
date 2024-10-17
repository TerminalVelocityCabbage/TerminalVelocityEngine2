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

    public Component getComponent() {
        return component;
    }

    public Vector3f getStartTransformation() {
        return startTransformation;
    }

    public Vector3f getEndTransformation() {
        return endTransformation;
    }

    public Easing.Function getEasingFunction() {
        return easingFunction;
    }

    public float getStartTime() {
        return startTime;
    }

    public float getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return "Keyframe{" +
                "component=" + component +
                ", startTransformation=" + startTransformation +
                ", endTransformation=" + endTransformation +
                ", easingFunction=" + easingFunction +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }

    public enum Component {
        POSITION,
        ROTATION,
        SCALE
    }
}
