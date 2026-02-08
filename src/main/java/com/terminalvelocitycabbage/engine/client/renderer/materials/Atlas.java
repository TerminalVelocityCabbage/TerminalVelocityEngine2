package com.terminalvelocitycabbage.engine.client.renderer.materials;

import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.filesystem.resources.Resource;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.util.ImageUtils;
import com.terminalvelocitycabbage.engine.util.MathUtils;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.system.MemoryUtil;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.Collectors;

public class Atlas extends Texture {

    private Map<Identifier, AtlasTexture> atlas;

    public Atlas(Map<Identifier, Resource> textureResources) {

        //Early exit for empty resources
        if (textureResources.isEmpty()) Log.crash("Cannot create atlas with no textures");

        //TODO determine if this is okay or not
//        //Early exit for single texture atlases
//        if (textureResources.size() == 1) {
//            Log.warn("Only one texture in atlas, using that texture directly instead of creating an atlas for it");
//            var entry = textureResources.entrySet().iterator().next();
//            var textureData = Data.fromResource(entry.getKey(), entry.getValue());
//            generateOpenGLTexture(textureData.width(), textureData.height(), textureData.components(), textureData.imageBuffer());
//            textureData.free();
//            return;
//        }

        //Generates all texture data into temporary data objects for use later
        Map<Identifier, Data> sortedTextureData = loadTextureData(textureResources);
        //Determines the size of the atlas from the texture data
        Vector2i atlasDimensions = getAtlasDimension(sortedTextureData);
        this.width = atlasDimensions.x;
        this.height = atlasDimensions.y;

        //Storage for individual texture info in this atlas
        this.atlas = new HashMap<>();

        //Start generating the atlas
        Map<Identifier, Vector2i> texturePositionsInAtlas = getTexturePositionsInAtlas(sortedTextureData);
        ByteBuffer atlasImageBuffer = MemoryUtil.memAlloc(atlasDimensions.x * atlasDimensions.y * 4);
        for (Map.Entry<Identifier, Vector2i> identifierVector2iEntry : texturePositionsInAtlas.entrySet()) {
            var textureIdentifier = identifierVector2iEntry.getKey();
            var texturePositionInAtlas = identifierVector2iEntry.getValue();
            var textureData = sortedTextureData.get(textureIdentifier);
            insertSubImage(atlasImageBuffer, textureData, texturePositionInAtlas);
            textureData.free();
        }

        //Set this Atlas' info
        generateOpenGLTexture(atlasDimensions.x, atlasDimensions.y, 4, atlasImageBuffer);

        //Test
        //ImageUtils.saveRGBAtoPNG(atlasImageBuffer, atlasDimensions.x, atlasDimensions.y, new File("atlas.png"));

        //Cleanup
        MemoryUtil.memFree(atlasImageBuffer);
    }

    /**
     * Inserts the subimage into this atlas buffer
     * @param atlas The atlas image buffer which this image is being added to
     * @param subImage The image buffer to be inserted
     * @param dest The location for the sub image buffer
     */
    private void insertSubImage(ByteBuffer atlas, Data subImage, Vector2i dest) {

        long atlasBaseAddr = MemoryUtil.memAddress(atlas);
        long subImageBaseAddr = MemoryUtil.memAddress(subImage.imageBuffer());

        for (int row = 0; row < subImage.height(); row++) {
            int destRow = dest.y() + row;
            if (destRow >= getHeight() || dest.x() + subImage.width() > getWidth()) continue;

            long destOffset = ((long) destRow * getWidth() + dest.x()) * 4;
            long srcOffset = ((long) row * subImage.width()) * 4;

            MemoryUtil.memCopy(
                    subImageBaseAddr + srcOffset,
                    atlasBaseAddr + destOffset,
                    subImage.width() * 4
            );
        }
    }

    /**
     * @param dataMap     The map of individual textures to pack into this atlas
     * @return A map of individual textures and their positions in this atlas
     */
    private Map<Identifier, Vector2i> getTexturePositionsInAtlas(Map<Identifier, Data> dataMap) {

        int currentX = 0;
        int currentY = 0;

        boolean[][] used = new boolean[getWidth()][getHeight()];
        Map<Identifier, Vector2i> texturePositionsInAtlas = new HashMap<>();

        //We want to pack the textures in reverse size order so that we know everything will fit
        List<Identifier> reverseKeys = new ArrayList<>(dataMap.keySet());
        Collections.reverse(reverseKeys);

        //Loop through all textures and mark their positions in the texture position map
        for (Identifier textureIdentifier : reverseKeys) {
            var data = dataMap.get(textureIdentifier);

            //Find the first available spot that fits this texture
            boolean found = false;
            for (int y = 0; y <= getHeight() - data.height(); y += 2) { //Step by 2 as a small optimization, all textures are power of 2
                for (int x = 0; x <= getWidth() - data.width(); x += 2) {
                    if (canFit(used, x, y, data.width())) {
                        //Mark this texture's position here
                        texturePositionsInAtlas.put(textureIdentifier, new Vector2i(x, y));
                        //Put this texture into the atlas info map
                        atlas.put(textureIdentifier, new AtlasTexture(x, y, data.width()));
                        //Mark the pixels that this texture used as used so we don't overwrite these pixels later
                        markUsed(used, x, y, data.width());
                        found = true;
                        break;
                    }
                }
                if (found) break;
            }

            if (!found) Log.crash("Failed to pack texture " + textureIdentifier + " into atlas, atlas is too small or packing is inefficient");
        }

        if (dataMap.size() != texturePositionsInAtlas.size()) Log.crash("Failed to pack all textures into atlas, not all textures were added");

        return texturePositionsInAtlas;
    }

    /**
     * @return true if the square block of size starting at (x, y) is empty
     */
    private static boolean canFit(boolean[][] used, int x, int y, int size) {
        for (int dx = 0; dx < size; dx++) {
            for (int dy = 0; dy < size; dy++) {
                if (used[x + dx][y + dy]) return false;
            }
        }
        return true;
    }

    /**
     * Marks a square block of size as used starting at (x, y)
     */
    private static void markUsed(boolean[][] used, int x, int y, int size) {
        for (int dx = 0; dx < size; dx++) {
            for (int dy = 0; dy < size; dy++) {
                used[x + dx][y + dy] = true;
            }
        }
    }


    /**
     * Determines the minimum square-ish size for this texture.
     * How?:
     *     To generate a tightly packed atlas, we need to know how big it needs to be first, To do this, we will figure out
     *     how many of the smallest texture there are and divide that by 4 and round up. We will do this for each power of
     *     2 between the min and max individual texture sizes and add that number to the next one up as we go. This will
     *     eventually equal the number of max size textures we need, which is easier to determine the atlas dimension from.
     * @param sortedTextureData The texture data that needs to fit into this atlas in order from largest to smallest
     * @return The size in pixels that this atlas will be
     */
    private static Vector2i getAtlasDimension(Map<Identifier, Data> sortedTextureData) {

        //Get min texture size in this atlas
        int minSize = sortedTextureData.values().iterator().next().width();
        int maxSize = 0;
        //Get max texture size in this atlas
        for (Data data : sortedTextureData.values()) {
            maxSize = data.width();
        }

        //Generate a Map of all sizes between the min and max for counting
        Map<Integer, Integer> sizeCounts = new LinkedHashMap<>();
        for (int i = minSize; i <= maxSize; i*=2) {
            sizeCounts.put(i, 0);
        }

        //Count each single texture size being added to this map
        for (Data data : sortedTextureData.values()) {
            sizeCounts.put(data.width(), sizeCounts.get(data.width()) + 1);
        }

        //Get the number of max-sized textures that this atlas needs by packing in smaller textures into it's size
        int numMaxSizeTextures = 0;
        int lastSizeLeftover = 0;
        List<Integer> keys = new ArrayList<>(sizeCounts.keySet());
        for (int i = 0; i < keys.size(); i++) {
            int size = keys.get(i);
            int count = sizeCounts.get(size);
            if (size == maxSize) {
                numMaxSizeTextures += count;
            } else {
                int totalCurrentSizeNeeded = count + lastSizeLeftover;
                numMaxSizeTextures += totalCurrentSizeNeeded / ( (maxSize / size) * (maxSize / size) );
                lastSizeLeftover = totalCurrentSizeNeeded % ( (maxSize / size) * (maxSize / size) );
            }
        }
        if (lastSizeLeftover > 0) numMaxSizeTextures++;

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
            if (textureData.width() != textureData.height()) Log.crash("Texture " + textureIdentifier + " is not square, cannot create atlas, it's: " + textureData.width() + "x" + textureData.height());
            if (!MathUtils.isPowerOfTwo(textureData.width())) Log.crash("Texture " + textureIdentifier + " is not a power of 2, cannot create atlas");
            //Add it to the list of textures to be added to this atlas
            unsortedTextureData.put(textureIdentifier, textureData);
        });

        //Sort Texture data by texture size, so it's simpler to insert it into the atlas
        return unsortedTextureData.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
    }

    public record AtlasTexture(int x, int y, int size) { }

    /**
     * Gets some information about a texture on this atlas
     * @param textureIdentifier The texture which you want information about
     * @return The Texture info for that texture
     */
    public AtlasTexture getTextureInfo(Identifier textureIdentifier) {
        return atlas.get(textureIdentifier);
    }

    /**
     * Transforms some UV coordinates to coordinates that represent the atlas region of the original texture given
     * @param textureIdentifier The texture that the UV coordinates we're transforming used to map to before it was
     *                          added to this atlas
     * @param modelUV The original UV coordinates to the original simple texture
     * @return A new set of UV coordinates to the atlas region that contains the existing texture
     */
    public Vector2f getTextureUVFromModelUV(Identifier textureIdentifier, Vector2f modelUV) {

        var atlasTexture = getTextureInfo(textureIdentifier);

        return new Vector2f(
                MathUtils.lerp(atlasTexture.x(), atlasTexture.x() + atlasTexture.size(), modelUV.x()) / getWidth(),
                MathUtils.lerp(atlasTexture.y(), atlasTexture.y() + atlasTexture.size(), modelUV.y()) / getHeight()
        );
    }

}
