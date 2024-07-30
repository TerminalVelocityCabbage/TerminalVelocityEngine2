package com.terminalvelocitycabbage.engine.client.renderer.animation;

import com.terminalvelocitycabbage.engine.util.Easing;
import com.terminalvelocitycabbage.engine.util.tuples.Pair;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.terminalvelocitycabbage.engine.client.renderer.animation.Keyframe.Component.*;

public class Animation {

    long time; //The time that this animation has been playing
    long currentTime;
    long startDelay;
    long animationLength;
    long loopDelay;
    long loopLength;

    //Bone name, keyframe
    private final Map<String, List<Keyframe>> keyframes;
    private final Map<String, Integer> boneIndexMap;

    Matrix4f transformationMatrix;

    public Animation(long startDelay, long animationLength, long loopDelay, Map<String, List<Keyframe>> keyframes, Map<String, Integer> boneIndexMap) {
        this.time = 0;
        this.startDelay = startDelay;
        this.animationLength = animationLength;
        this.loopDelay = loopDelay;
        this.loopLength = animationLength + loopDelay;
        this.keyframes = keyframes;
        this.boneIndexMap = boneIndexMap;
    }

    public void updateTime(long deltaTime) {

        time += deltaTime;
        //Get the current position in the looping keyframe
        currentTime = time - startDelay;
        currentTime %= loopLength;
    }

    //Percentage through keyframe, the target transformation
    public List<Keyframe> getCurrentKeyframes(String boneName) {

        List<Keyframe> currentKeyFrames = new ArrayList<>();

        for (Keyframe keyframe : keyframes.get(boneName)) {
            if (currentTime > keyframe.startTime && currentTime < keyframe.endTime) currentKeyFrames.add(keyframe);
        }

        return currentKeyFrames;
    }

    //Bone name, transformations
    public Map<String, Matrix4f> getCurrentTransformations() {

        Map<String, Matrix4f> currentTransformations = new HashMap<>();
        List<Pair<Float, Keyframe>> progressKeyframes = new ArrayList<>();
        for (String boneName : keyframes.keySet()) {
            for (Keyframe keyframe : getCurrentKeyframes(boneName)) {
                progressKeyframes.add(new Pair<>(getKeyframeProgress( keyframe), keyframe));
            }
            currentTransformations.put(boneName, getTransformationMatrices(progressKeyframes));
        }

        return currentTransformations;
    }

    /**
     * Return the value from 0 to 1 that defines the progress between KS and KE for this animation
     *
     * AS = animation start
     * KS = keyframe start time
     * CT = currentTime
     * KE = keyframe end time
     *     AS          KS   CT KE
     *     |-----------|----x--|-----------...
     *
     * to make the math easier we should just subtract everything by KS
     * KS = KS - KS (0)
     * CT = CT - KS
     * KE = KE - AS
     *
     * percentage is just CT / KE so
     * (CT - KS) / (KE - AS) = percentage
     *
     * @param keyframe The keyframe we want the progress of
     * @return the progress from 0 to 1 of this keyframe
     */
    public float getKeyframeProgress(Keyframe keyframe) {
        return (currentTime - keyframe.startTime) / (keyframe.endTime - keyframe.startTime);
    }

    public Matrix4f getTransformationMatrices(List<Pair<Float, Keyframe>> keyframes) {

        transformationMatrix.identity();

        //combine all keyframe transformations into one
        Vector3f position = new Vector3f();
        Vector3f rotation = new Vector3f();
        Vector3f scale = new Vector3f();

        for (Pair<Float, Keyframe> entry : keyframes) {
            float progress = entry.getValue0();
            Keyframe keyframe = entry.getValue1();
            switch (keyframe.component) {
                case POSITION -> interpolateComponent(position, progress, keyframe, POSITION);
                case ROTATION -> interpolateComponent(position, progress, keyframe, ROTATION);
                case SCALE -> interpolateComponent(position, progress, keyframe, SCALE);
            }
        }

        //Transform the matrix
        transformationMatrix.scale(scale);
        transformationMatrix.rotateXYZ(rotation);
        transformationMatrix.translate(position);

        return transformationMatrix;
    }

    private void interpolateComponent(Vector3f transformation, float progress, Keyframe keyframe, Keyframe.Component component) {
        //TODO set to start transformation if end is null or 0 instead of calculating all 9 components of below if there is no need for any of them
        transformation.add(
                Easing.easeInOut(keyframe.easingFunction, progress) * (keyframe.endTransformation.x() - keyframe.startTransformation.x()),
                Easing.easeInOut(keyframe.easingFunction, progress) * (keyframe.endTransformation.y() - keyframe.startTransformation.y()),
                Easing.easeInOut(keyframe.easingFunction, progress) * (keyframe.endTransformation.y() - keyframe.startTransformation.y())
        );
    }
}
