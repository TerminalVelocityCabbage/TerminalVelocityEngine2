package com.terminalvelocitycabbage.engine.client.renderer.animation;

import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Animation {

    long time; //The time that this animation has been playing
    long currentTime;
    long startDelay;
    long animationLength;
    long loopDelay;
    long loopLength;

    //Bone name, keyframe
    private final Map<String, List<Keyframe>> keyframes;

    public Animation(long time, long startDelay, long animationLength, long loopDelay, Map<String, List<Keyframe>> keyframes) {
        this.time = time;
        this.startDelay = startDelay;
        this.animationLength = animationLength;
        this.loopDelay = loopDelay;
        this.loopLength = animationLength + loopDelay;
        this.keyframes = keyframes;
    }

    public void updateTime(long deltaTime) {

        time += deltaTime;
        //Get the current position in the looping keyframe
        currentTime = time - startDelay;
        currentTime %= loopLength;
    }

    //Percentage through keyframe, the target transformation
    public Keyframe getCurrentKeyframe(String boneName) {

        Keyframe currentKeyframe = keyframes.get(boneName).getFirst();
        for (Keyframe keyframe : keyframes.get(boneName)) {
            if (currentTime > keyframe.startTime) currentKeyframe = keyframe;
        }

        return currentKeyframe;
    }

    //Bone name, transformations
    public Map<String, Matrix4f> getCurrentTransformations() {

        Map<String, Matrix4f> currentTransformations = new HashMap<>();
        Keyframe currentKeyframe;
        float currentKeyframeProgress;
        for (String boneName : keyframes.keySet()) {
            currentKeyframe = getCurrentKeyframe(boneName);
            currentKeyframeProgress = getCurrentKeyframeProgress(currentKeyframe);
            currentTransformations.put(boneName, currentKeyframe.getTransformationMatrix(currentKeyframeProgress));
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
    public float getCurrentKeyframeProgress(Keyframe keyframe) {
        return (currentTime - keyframe.startTime) / (keyframe.endTime - keyframe.startTime);
    }
}
