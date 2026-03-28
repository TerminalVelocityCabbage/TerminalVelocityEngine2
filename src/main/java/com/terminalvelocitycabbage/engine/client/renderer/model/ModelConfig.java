package com.terminalvelocitycabbage.engine.client.renderer.model;

import org.joml.Matrix4f;
import java.util.List;

public record ModelConfig(List<MeshTexturePair> meshTexturePairs, Skeleton skeleton) {

    public ModelConfig(List<MeshTexturePair> meshTexturePairs) {
        this(meshTexturePairs, null);
    }

}
