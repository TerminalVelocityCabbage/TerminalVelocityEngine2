package com.terminalvelocitycabbage.engine.client.renderer.animation;

import com.terminalvelocitycabbage.engine.util.Easing;
import com.terminalvelocitycabbage.engine.util.tuples.Pair;
import com.terminalvelocitycabbage.engine.util.tuples.Triplet;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Animation {

    long time; //The time that this animation has been playing
    long currentTime;
    final long startDelay;
    final long animationLength;
    final long loopDelay;
    final long loopLength;

    //Status
    boolean isPlaying;
    int playsRemaining; //-1 if indefinite
    boolean startFromZero;

    //Bone name, keyframe
    private final Map<String, List<Keyframe>> keyframes;

    public Animation(long startDelay, long animationLength, long loopDelay, Map<String, List<Keyframe>> keyframes) {
        this.time = 0;
        this.currentTime = 0;
        this.startDelay = startDelay;
        this.animationLength = animationLength;
        this.loopDelay = loopDelay;
        this.loopLength = animationLength + loopDelay;
        this.isPlaying = false;
        this.playsRemaining = 0;
        this.startFromZero = false;
        this.keyframes = keyframes;
    }

    public void update(long deltaTime) {

        //Get the current position in the looping keyframe
        if (!isPlaying) {

            time = startFromZero ? 0 : time + deltaTime;
            currentTime = time - startDelay;

            if (playsRemaining != -1 && currentTime > animationLength) {
                playsRemaining = Math.max(playsRemaining - 1, 0);
            }

            if (playsRemaining == 0) {
                currentTime = animationLength + startDelay;
            } else {
                currentTime %= loopLength;
            }
        }

        startFromZero = false;
    }

    public void play() {
        play(true);
    }

    public void play(boolean startOver) {
        this.startFromZero = startOver;
        isPlaying = true;
        playsRemaining = 1;
    }

    public void repeat(int timesToRepeat) {
        isPlaying = true;
        playsRemaining = timesToRepeat;
    }

    public void loop() {
        isPlaying = true;
        playsRemaining = -1;
    }

    public void pause() {
        isPlaying = false;
    }

    public void resume() {
        isPlaying = true;
    }

    public void stop() {
        isPlaying = false;
        playsRemaining = 0;
        time = 0;
        currentTime = 0;
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
    Map<String, Triplet<Vector3f, Vector3f, Vector3f>> currentTransformations = new HashMap<>();
    List<Pair<Float, Keyframe>> progressKeyframes = new ArrayList<>();
    public Map<String, Triplet<Vector3f, Vector3f, Vector3f>> getCurrentTransformations() {
        currentTransformations.clear();
        progressKeyframes.clear();
        for (String boneName : keyframes.keySet()) {
            for (Keyframe keyframe : getCurrentKeyframes(boneName)) {
                progressKeyframes.add(new Pair<>(getKeyframeProgress(keyframe), keyframe));
            }
            currentTransformations.put(boneName, getCurrentTransforms(progressKeyframes));
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
    float getKeyframeProgress(Keyframe keyframe) {
        return (currentTime - keyframe.startTime) / (keyframe.endTime - keyframe.startTime);
    }

    Triplet<Vector3f, Vector3f, Vector3f> getCurrentTransforms(List<Pair<Float, Keyframe>> keyframes) {

        //combine all keyframe transformations into one
        Vector3f position = new Vector3f();
        Vector3f rotation = new Vector3f();
        Vector3f scale = new Vector3f(1);

        for (Pair<Float, Keyframe> entry : keyframes) {
            float progress = entry.getValue0();
            Keyframe keyframe = entry.getValue1();
            switch (keyframe.component) {
                case POSITION -> interpolateComponent(position, progress, keyframe);
                case ROTATION -> interpolateComponent(rotation, progress, keyframe);
                case SCALE -> interpolateComponent(scale, progress, keyframe);
            }
        }

        return new Triplet<>(position, rotation, scale);
    }

    void interpolateComponent(Vector3f transformation, float progress, Keyframe keyframe) {
        var xTransform = keyframe.endTransformation.x() == 0 ? keyframe.startTransformation.x() : Easing.easeInOut(keyframe.easingFunction, progress) * (keyframe.endTransformation.x() - keyframe.startTransformation.x());
        var yTransform = keyframe.endTransformation.y() == 0 ? keyframe.startTransformation.y() : Easing.easeInOut(keyframe.easingFunction, progress) * (keyframe.endTransformation.y() - keyframe.startTransformation.y());
        var zTransform = keyframe.endTransformation.z() == 0 ? keyframe.startTransformation.z() : Easing.easeInOut(keyframe.easingFunction, progress) * (keyframe.endTransformation.z() - keyframe.startTransformation.z());
        transformation.add(xTransform, yTransform, zTransform);
    }
}
