package com.terminalvelocitycabbage.engine.client.renderer.materials;

import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.filesystem.resources.Resource;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.util.ImageUtils;
import com.terminalvelocitycabbage.engine.util.MathUtils;
import com.terminalvelocitycabbage.engine.util.touples.Triplet;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.system.MemoryUtil;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.Collectors;

public class Atlas extends SingleTexture {

    //Identifier of the registered texture in this Atlas -> X pos, Y pos, Size
    Map<Identifier, Triplet<Integer, Integer, Integer>> atlas;

    public Atlas(Map<Identifier, Resource> textureResources) {

        //Early exit for empty resources
        if (textureResources.isEmpty()) Log.crash("Cannot create atlas with no textures");

        //Early exit for single texture atlases
        if (textureResources.size() == 1) {
            Log.warn("Only one texture in atlas, using that texture directly instead of creating an atlas for it");
            var entry = textureResources.entrySet().iterator().next();
            var textureData = Data.fromResource(entry.getKey(), entry.getValue());
            generateOpenGLTexture(textureData.width(), textureData.height(), textureData.components(), textureData.imageBuffer());
            textureData.free();
            return;
        }

        //Generates all texture data into temporary data objects for use later
        Map<Identifier, Data> sortedTextureData = loadTextureData(textureResources);
        //Determines the size of the atlas from the texture data
        Vector2i atlasDimensions = getAtlasDimension(sortedTextureData);

        //Start generating the atlas
        Map<Identifier, Vector2i> texturePositionsInAtlas = getTexturePositionsInAtlas(sortedTextureData, atlasDimensions.x, atlasDimensions.y);
        ByteBuffer atlasImageBuffer = MemoryUtil.memAlloc(atlasDimensions.x * atlasDimensions.y * 4);
        for (Map.Entry<Identifier, Vector2i> identifierVector2iEntry : texturePositionsInAtlas.entrySet()) {
            var textureIdentifier = identifierVector2iEntry.getKey();
            var texturePositionInAtlas = identifierVector2iEntry.getValue();
            var textureData = sortedTextureData.get(textureIdentifier);
            insertSubImage(atlasImageBuffer, atlasDimensions, textureData, texturePositionInAtlas);
            textureData.free();
        }

        //Set this Atlas' info
        this.width = atlasDimensions.x;
        this.height = atlasDimensions.y;
        generateOpenGLTexture(atlasDimensions.x, atlasDimensions.y, 4, atlasImageBuffer);

        //Test
        ImageUtils.saveRGBAtoPNG(atlasImageBuffer, atlasDimensions.x, atlasDimensions.y, new File("atlas.png"));

        //Cleanup
        MemoryUtil.memFree(atlasImageBuffer);
    }

    /**
     * Inserts a subImage into the atlas using fast memCopy.
     */
    private static void insertSubImage(ByteBuffer atlas, Vector2i atlasDimensions, Data subImage, Vector2i dest) {

        long atlasBaseAddr = MemoryUtil.memAddress(atlas);
        long subImageBaseAddr = MemoryUtil.memAddress(subImage.imageBuffer());

        for (int row = 0; row < subImage.height(); row++) {
            int destRow = dest.y() + row;
            if (destRow >= atlasDimensions.y() || dest.x() + subImage.width() > atlasDimensions.x()) continue;

            long destOffset = ((long) destRow * atlasDimensions.x() + dest.x()) * 4;
            long srcOffset = ((long) row * subImage.width()) * 4;

            MemoryUtil.memCopy(
                    subImageBaseAddr + srcOffset,
                    atlasBaseAddr + destOffset,
                    subImage.width() * 4
            );
        }
    }

    private static Map<Identifier, Vector2i> getTexturePositionsInAtlas(Map<Identifier, Data> dataMap, int atlasWidth, int atlasHeight) {

        int currentX = 0;
        int currentY = 0;

        boolean[][] used = new boolean[atlasHeight][atlasWidth];
        Map<Identifier, Vector2i> texturePositionsInAtlas = new HashMap<>();

        List<Identifier> reverseKeys = new ArrayList<>(dataMap.keySet());
        Collections.reverse(reverseKeys);
        for (Identifier textureIdentifier : reverseKeys) {
            var data = dataMap.get(textureIdentifier);

            if (data.width() + currentX > atlasWidth) {
                currentY += data.height();
                for (int i = 0; i < atlasWidth; i++) {
                    if (!used[i][currentY]) {
                        currentX = i;
                        break;
                    }
                }
            }
            if (data.width() + currentX <= atlasWidth) {
                texturePositionsInAtlas.put(textureIdentifier, new Vector2i(currentX, currentY));
                markUsed(used, currentX, currentY, data.width());
                currentX += data.width();
            }
        }

        if (dataMap.size() != texturePositionsInAtlas.size()) Log.crash("Failed to pack all textures into atlas, not all textures were added");

        return texturePositionsInAtlas;
    }

    /**
     * Marks a square block of size as used starting at (x, y)
     */
    private static void markUsed(boolean[][] used, int x, int y, int size) {
        for (int dy = 0; dy < size; dy++) {
            for (int dx = 0; dx < size; dx++) {
                used[y + dy][x + dx] = true;
            }
        }
    }

    private static Vector2i getAtlasDimension(Map<Identifier, Data> sortedTextureData) {
        /*
        To generate a tightly packed atlas, we need to know how big it needs to be first, To do this, we will figure out
        how many of the smallest texture there are and divide that by 4 and round up. We will do this for each power of
        2 between the min and max individual texture sizes and add that number to the next one up as we go. This will
        eventually equal the number of max size textures we need, which is easier to determine the atlas dimension from.
         */

        //Get min texture size in this atlas
        int minSize = sortedTextureData.values().iterator().next().width();
        Log.info(minSize);
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
        for (Data data : sortedTextureData.values()) {
            sizeCounts.put(data.width(), sizeCounts.get(data.width()) + 1);
        }

        //Get the number of max-sized textures that this atlas needs
        int numMaxSizeTextures = 0;
        for (Map.Entry<Integer, Integer> entry : sizeCounts.entrySet()) {
            numMaxSizeTextures = Math.ceilDiv(entry.getValue() + numMaxSizeTextures, 4);
        }

        //Determine size of atlas
        Vector2i maxTextureSizeAtlasDimensions = MathUtils.findMostSquareDimensions(numMaxSizeTextures);
        return new Vector2i(maxTextureSizeAtlasDimensions.x * maxSize, maxTextureSizeAtlasDimensions.y * maxSize);
    }

    /**
     * @param textureResources The texture resources that this atlas is going to be comprised of
     * @return A list of these resource identifiers and the loaded associated data in order of smallest to largest sizes
     */
    private static Map<Identifier, Data> loadTextureData(Map<Identifier, Resource> textureResources) {
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
        return sortedTextureData;
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
