package com.terminalvelocitycabbage.engine.client.renderer.animation;

import com.terminalvelocitycabbage.engine.client.renderer.model.Model;
import com.terminalvelocitycabbage.engine.util.tuples.Pair;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Animation {

    long time; //The time that this animation has been playing
    long startDelay;
    long animationLength;
    long loopDelay;
    long loopLength;

    //Map of boneName, <keyframe starts, transformations>
    private final Map<String, Pair<List<Float>, List<AnimationTransformation>>> transformations;

    public Animation(Map<String, Pair<List<Float>, List<AnimationTransformation>>> transformations, long time, long startDelay, long animationLength, long loopDelay) {
        this.transformations = transformations;
        this.time = time;
        this.startDelay = startDelay;
        this.animationLength = animationLength;
        this.loopDelay = loopDelay;
        this.loopLength = animationLength + loopDelay;
    }

    public void updateTime(long deltaTime) {
        time += deltaTime;
    }

    //Percentage through keyframe, the target transformation
    //TODO also return in this the previous transforms
    public Pair<Float, AnimationTransformation> getCurrentTransform(String boneName) {

        //Get the current position in the looping keyframe
        long currentTime = time - startDelay;
        currentTime %= loopLength;

        //Get the current keyframe based on this position
        int currentFrameIndex = 0;

        Pair<List<Float>, List<AnimationTransformation>> startsTransformsList = transformations.get(boneName);
        List<Float> transformationStarts = startsTransformsList.getValue0();
        List<AnimationTransformation> animationTransformations = startsTransformsList.getValue1();
        for (int i = 0; i < transformationStarts.size(); i++) {
            if (currentTime >= transformationStarts.get(i)) {
                currentFrameIndex = i;
            }
        }

        //Get current percentage through this keyframe
        float startTime = transformationStarts.get(currentFrameIndex);
        float nextStartTime;
        if (startsTransformsList.getValue0().size() > currentFrameIndex + 1) {
            nextStartTime = startsTransformsList.getValue0().get(currentFrameIndex + 1);
        } else { //If this is the last keyframe
            nextStartTime = loopLength - startsTransformsList.getValue0().get(currentFrameIndex);
        }
        float percentComplete = currentTime / (nextStartTime - startTime);
        var targetTransformation = animationTransformations.get(currentFrameIndex);

        return new Pair<>(percentComplete, targetTransformation);
    }

    public Map<String, Matrix4f> getCurrentTransformations(Model model) {

        //Get all the current target transformations
        //TODO change this to a map with a triplet including the previous transforms
        Map<String, Pair<Float, AnimationTransformation>> relevantTransforms = new HashMap<>();
        for (String boneName : transformations.keySet()) {
            relevantTransforms.put(boneName, getCurrentTransform(boneName));
        }

        //Interpolate these targets with the previous keyframes values and the progress between these two
        Map<String, Matrix4f> transforms = new HashMap<>();
        Matrix4f transformationMatrix = new Matrix4f();
        for (Model.Part part : model.getParts()) {
            //TODO interpolate current transforms by progress compared to previous transforms
            //TODO get default transforms of model part and add current transforms
            //TODO create matrix4f from this
        }

        return transforms;
    }
}
