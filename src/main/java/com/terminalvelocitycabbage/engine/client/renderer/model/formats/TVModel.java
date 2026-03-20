package com.terminalvelocitycabbage.engine.client.renderer.model.formats;

import com.github.zafarkhaja.semver.Version;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.util.touples.Pair;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public record TVModel(
        TVModelMetadata metadata,
        Map<String, TVModelVariant> variants,
        Map<String, TVModelBone> bones,
        Map<String, TVModelCube> cubes,
        Map<String, TVModelAnchor> anchors
) {

    public record TVModelMetadata(
            Version version,
            List<String> textureLayers,
            TVModelVariant defaultVariant
    ) { }

    public record TVModelVariant(
            String name,
            String parent,
            List<String> bones,
            List<String> excludedBones,
            Map<String, Identifier> textures //Layer -> texture
    ) { }

    public record TVModelBone(
            String name,
            Optional<String> parent,
            Vector3f position,
            Vector3f offset,
            Vector3f rotation
    ) { }

    public record TVModelCube(
            String name, //The name of this cube
            Optional<String> parent, //The name of the parent cube or bone, if any.
            Vector3i size,
            Vector3f grow,
            Vector3f position,
            Vector3f offset,
            Vector3f rotation,
            TVModelCubeTextureMapping textures
    ) { }

    public record TVModelCubeTextureMapping(
            String layer,
            Optional<Pair<Vector2i, Vector2i>> pxFace, //Positive X Face (right/east)
            Optional<Pair<Vector2i, Vector2i>> nxFace, //Negative X Face (left/west)
            Optional<Pair<Vector2i, Vector2i>> pyFace, //Positive Y Face (up/top)
            Optional<Pair<Vector2i, Vector2i>> nyFace, //Negative Y Face (down/bottom)
            Optional<Pair<Vector2i, Vector2i>> pzFace, //Positive Z Face (forward/north)
            Optional<Pair<Vector2i, Vector2i>> nzFace  //Negative Z Face (back/south)
    ) { }

    public record TVModelAnchor(
            String name,
            Optional<String> parent,
            Vector3f position,
            Vector3f offset,
            Vector3f rotation
    ) { }

}
