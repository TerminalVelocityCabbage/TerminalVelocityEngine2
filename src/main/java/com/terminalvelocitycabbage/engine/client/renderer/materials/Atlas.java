package com.terminalvelocitycabbage.engine.client.renderer.materials;

import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.filesystem.resources.Resource;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.util.MathUtils;
import com.terminalvelocitycabbage.engine.util.touples.Triplet;
import org.joml.Vector2f;
import org.joml.Vector2i;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Atlas extends SingleTexture {

    //Identifier of the registered texture in this Atlas -> X pos, Y pos, Size
    Map<Identifier, Triplet<Integer, Integer, Integer>> atlas;

    public Atlas(Map<Identifier, Resource> textureResources) {

        //Early exit for empty resources
        if (textureResources.isEmpty()) Log.crash("Cannot create atlas with no textures");

        //Generate texture data from resources for each texture and validate it is compatible with the texture atlas
        Map<Identifier, Data> unsortedTextureData = new HashMap<>();
        textureResources.forEach((textureIdentifier, textureResource) -> {
            //Get this texture's data
            var textureData = Data.fromResource(textureIdentifier, textureResource);
            //Verify all textures are square and power of 2
            if (textureData.width() != textureData.height()) Log.crash("Texture " + textureIdentifier + " is not square, cannot create atlas");
            if (!MathUtils.isPowerOfTwo(textureData.width())) Log.crash("Texture " + textureIdentifier + " is not a power of 2, cannot create atlas");
            //Add it to the list of textures to be added to this atlas
            unsortedTextureData.put(textureIdentifier, textureData);
        });

        //Sort Texture data by texture size, so it's simpler to insert it into the atlas
        Map<Identifier, Data> sortedTextureData = unsortedTextureData.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));

        /*
        To generate a tightly packed atlas, we need to know how big it needs to be first, To do this, we will figure out
        how many of the smallest texture there are and divide that by 4 and round up. We will do this for each power of
        2 between the min and max individual texture sizes and add that number to the next one up as we go. This will
        eventually equal the number of max size textures we need, which is easier to determine the atlas dimension from.
         */

        //Get min texture size in this atlas
        int minSize = sortedTextureData.values().iterator().next().width();
        int maxSize = 0;
        //Get max texture size in this atlas
        for (Data data : sortedTextureData.values()) {
            maxSize = data.width();
        }

        //Generate a Map of all sizes between the min and max for counting
        Map<Integer, Integer> sizeCounts = new LinkedHashMap<>();
        for (int i = minSize; i < maxSize; i*=2) {
            sizeCounts.put(i, 0);
        }

        //Count each single texture size being added to this map
        sortedTextureData.values().forEach(data -> sizeCounts.put(data.width(), sizeCounts.get(data.width()) + 1));

        //Get the number of max-sized textures that this atlas needs
        int numMaxSizeTextures = 0;
        for (Map.Entry<Integer, Integer> entry : sizeCounts.entrySet()) {
            numMaxSizeTextures = Math.ceilDiv(entry.getValue() + numMaxSizeTextures, 4);
        }

        //Determine size of atlas
        Vector2i maxTextureSizeAtlasDimensions = MathUtils.findMostSquareDimensions(numMaxSizeTextures);
        Vector2i atlasDimensions = new Vector2i(maxTextureSizeAtlasDimensions.x * maxSize, maxTextureSizeAtlasDimensions.y * maxSize);

        //TODO Generate Atlas here

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
