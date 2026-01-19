package com.terminalvelocitycabbage.engine.client.renderer.model.bedrock;

import com.terminalvelocitycabbage.engine.ecs.Component;
import org.joml.Matrix4f;

import java.util.Map;

/**
 * A component that stores the state of a Bedrock-style skeletal animation.
 * This includes the loaded animations, the currently playing animation, and the
 * calculated bone transformations to be passed to the shader.
 */
public class AnimationComponent implements Component {

    /** The collection of animations available for this model. */
    public BedrockAnimations animations;
    /** The identifier of the currently playing animation. */
    public String currentAnimation;
    /** The current time elapsed in the active animation (in seconds). */
    public float currentTime;
    /** Whether the animation is currently playing. */
    public boolean playing;
    
    /** Final bone transforms calculated by {@link AnimationSystem}, ready for the shader. */
    public Matrix4f[] boneTransforms;
    
    /** Reference to the Bedrock geometry, used to determine the bone hierarchy and bind poses. */
    public BedrockGeometry geometry;
    
    /** A mapping from bone name to its index in the boneTransforms array. */
    public Map<String, Integer> boneIndexMap;

    public AnimationComponent() {
        setDefaults();
    }

    /**
     * Resets this component to its default state.
     */
    @Override
    public void setDefaults() {
        currentTime = 0;
        playing = false;
        currentAnimation = null;
        animations = null;
        boneTransforms = null;
        boneIndexMap = null;
        geometry = null;
    }
}
