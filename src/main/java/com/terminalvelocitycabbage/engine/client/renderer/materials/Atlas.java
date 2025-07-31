package com.terminalvelocitycabbage.engine.client.renderer.materials;

import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.util.MathUtils;
import com.terminalvelocitycabbage.engine.util.touples.Triplet;
import org.joml.Vector2f;
import org.joml.Vector2i;

import java.util.List;
import java.util.Map;

public class Atlas implements SingleTexture {

    int textureID;
    int width;
    int height;

    //Identifier of the registered texture in this Atlas -> X pos, Y pos, Size
    Map<Identifier, Triplet<Integer, Integer, Integer>> atlas;

    public Atlas(Identifier atlasIdentifier, List<Identifier> textureIdentifiers) {
        //TODO pack textures into single byte buffer and pass to generate function
        generateOpenGLTexture(atlasIdentifier, 0, 0, 0, null);
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getTextureID() {
        return textureID;
    }

    public Vector2i getTexturePosition(Identifier textureIdentifier) {
        var texture = atlas.get(textureIdentifier);
        return new Vector2i(texture.getValue0(), texture.getValue1());
    }

    public int getTextureX(Identifier textureIdentifier) {
        return atlas.get(textureIdentifier).getValue0();
    }

    public int getTextureY(Identifier textureIdentifier) {
        return atlas.get(textureIdentifier).getValue1();
    }

    public int getTextureSize(Identifier textureIdentifier) {
        return atlas.get(textureIdentifier).getValue2();
    }

    public Vector2f getTextureUVFromModelUV(Identifier textureIdentifier, int u, int v) {

        var xPos = getTextureX(textureIdentifier);
        var yPos = getTextureY(textureIdentifier);
        var size = getTextureSize(textureIdentifier);

        return new Vector2f(
                MathUtils.linearInterpolate(xPos, 0, xPos + size, 1, u),
                MathUtils.linearInterpolate(yPos, 0, yPos + size, 1, v)
        );
    }

}
