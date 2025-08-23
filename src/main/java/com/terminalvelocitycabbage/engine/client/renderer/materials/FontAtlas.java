package com.terminalvelocitycabbage.engine.client.renderer.materials;

import com.terminalvelocitycabbage.engine.client.ui.Font;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.util.ImageUtils;
import com.terminalvelocitycabbage.engine.util.MathUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.freetype.FT_Bitmap;
import org.lwjgl.util.freetype.FreeType;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class FontAtlas extends Texture {

    private final Map<Integer, GlyphInfo> glyphMap = new HashMap<>();

    public FontAtlas(Font font, int[] glyphIds) {

        //Determine the likely size of atlas (cross the bridge when we get to it if we're wrong)
        var atlasSize = MathUtils.findNearestPowerOfTwo((int) Math.ceil(Math.sqrt(font.getFontSize() * font.getFontSize() * glyphIds.length)));
        this.width = atlasSize;
        this.height = atlasSize;

        //Create the buffer for this atlas
        ByteBuffer atlasBuffer = ByteBuffer.allocateDirect(this.width * this.height);

        int x = 0, y = 0, rowHeight = 0;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            for (int glyphId : glyphIds) {
                // Load glyph into FreeType
                FreeType.FT_Load_Glyph(font.getFace(), glyphId, FreeType.FT_LOAD_RENDER);
                FT_Bitmap bitmap = font.getFace().glyph().bitmap();

                if (x + bitmap.width() >= getWidth()) {
                    x = 0;
                    y += rowHeight;
                    rowHeight = 0;
                }

                // Copy glyph bitmap into atlas
                ByteBuffer bitmapBuffer = bitmap.buffer(bitmap.rows() * bitmap.pitch());
                for (int row = 0; row < bitmap.rows(); row++) {
                    for (int col = 0; col < bitmap.width(); col++) {
                        atlasBuffer.put((y + row) * getWidth() + (x + col), bitmapBuffer.get(row * bitmap.pitch() + col));
                    }
                }

                // Record glyph info
                glyphMap.put(glyphId, new GlyphInfo(x, y, bitmap.width(), bitmap.rows(), font.getFace().glyph().advance().x() / 64f));

                x += bitmap.width() + 1;
                rowHeight = Math.max(rowHeight, bitmap.rows());
            }
        }

        // Upload to OpenGL
        Log.info("generateOpenGLTexture");
        generateOpenGLTexture(width, height, 4, atlasBuffer);

        Log.info("Saving font atlas to file");
        ImageUtils.saveRGBAtoPNG(atlasBuffer, width, height, new File("font_atlas.png"));
    }

    public GlyphInfo getGlyphInfo(int glyphId) {
        return glyphMap.get(glyphId);
    }

    public record GlyphInfo(int x, int y, int width, int height, float xAdvance) { }

}
