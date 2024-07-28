package com.terminalvelocitycabbage.engine.client.renderer.animation;

import com.terminalvelocitycabbage.engine.util.Easing;
import org.joml.Matrix4f;

/**
 * Represents a portion of a transformation between two target transformations.
 */
public class Keyframe {

    Easing.Function easingFunction;
    AnimationTransformation startTransformation;
    AnimationTransformation endTransformation;
    float startTime;
    float endTime;

    Matrix4f transformationMatrix;

    public Keyframe(Easing.Function easingFunction, AnimationTransformation startTransformation, AnimationTransformation endTransformation, float startTime, float endTime) {
        this.easingFunction = easingFunction;
        this.startTransformation = startTransformation;
        this.endTransformation = endTransformation;
        this.startTime = startTime;
        this.endTime = endTime;
        transformationMatrix = new Matrix4f();
    }

    public Matrix4f getTransformationMatrix(float progress) {

        transformationMatrix.identity();

        transformationMatrix.scale(
                Easing.easeInOut(easingFunction, progress) * ((endTransformation.scale().x) - (startTransformation.scale().x)),
                Easing.easeInOut(easingFunction, progress) * ((endTransformation.scale().y) - (startTransformation.scale().y)),
                Easing.easeInOut(easingFunction, progress) * ((endTransformation.scale().z) - (startTransformation.scale().z))
        );
        transformationMatrix.rotateXYZ(
                Easing.easeInOut(easingFunction, progress) * ((endTransformation.rotation().x) - (startTransformation.rotation().x)),
                Easing.easeInOut(easingFunction, progress) * ((endTransformation.rotation().y) - (startTransformation.rotation().y)),
                Easing.easeInOut(easingFunction, progress) * ((endTransformation.rotation().z) - (startTransformation.rotation().z))
        );
        transformationMatrix.translate(
                Easing.easeInOut(easingFunction, progress) * ((endTransformation.position().x) - (startTransformation.position().x)),
                Easing.easeInOut(easingFunction, progress) * ((endTransformation.position().y) - (startTransformation.position().y)),
                Easing.easeInOut(easingFunction, progress) * ((endTransformation.position().z) - (startTransformation.position().z))
        );
        return transformationMatrix;
    }

}
