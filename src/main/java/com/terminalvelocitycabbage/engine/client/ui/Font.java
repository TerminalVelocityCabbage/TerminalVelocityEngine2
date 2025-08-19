package com.terminalvelocitycabbage.engine.client.ui;

import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.filesystem.resources.Resource;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.freetype.FT_Face;
import org.lwjgl.util.freetype.FreeType;
import org.lwjgl.util.harfbuzz.HarfBuzz;
import org.lwjgl.util.harfbuzz.hb_glyph_info_t;
import org.lwjgl.util.harfbuzz.hb_glyph_position_t;

import java.util.stream.IntStream;

public class Font {

    private final int fontSize; //In pixels
    private final long freeType;
    private final long facePointer;
    private final FT_Face face;
    private final long font;

    public static final int[] ISO_8859_1_GLYPH_IDS = IntStream.rangeClosed(32, 126).toArray();

    public Font(Resource resource, int fontSize) {

        this.fontSize = fontSize;

        //Init freetype for font loading
        try (MemoryStack stack = MemoryStack.stackPush()) {
            //Init freetype and store pointer to library for later
            PointerBuffer ftPointer = stack.callocPointer(1);
            FreeType.FT_Init_FreeType(ftPointer);
            freeType = ftPointer.get(0);

            //Load font
            PointerBuffer facePointerBuffer = stack.callocPointer(1);
            FreeType.FT_New_Memory_Face(freeType, resource.asByteBuffer(true), 0, facePointerBuffer);
            facePointer = facePointerBuffer.get(0);

            //Configure font
            face = FT_Face.create(this.facePointer);
            FreeType.FT_Set_Pixel_Sizes(face, 0, fontSize);
        }

        //Init harfbuzz
        font = HarfBuzz.hb_ft_font_create_referenced(facePointer);
    }

    public ShapedText shapeText(String text) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            long buffer = HarfBuzz.hb_buffer_create();
            HarfBuzz.hb_buffer_add_utf8(buffer, text, 0, text.length());
            HarfBuzz.hb_buffer_guess_segment_properties(buffer);

            HarfBuzz.hb_shape(font, buffer, null);

            int glyphCount = HarfBuzz.hb_buffer_get_length(buffer);
            hb_glyph_info_t.Buffer infos = HarfBuzz.hb_buffer_get_glyph_infos(buffer);
            hb_glyph_position_t.Buffer positions = HarfBuzz.hb_buffer_get_glyph_positions(buffer);

            ShapedText shaped = new ShapedText(glyphCount);
            for (int i = 0; i < glyphCount; i++) {
                int glyphId = infos.get(i).codepoint();
                float xAdvance = positions.get(i).x_advance() / 64f;
                float yAdvance = positions.get(i).y_advance() / 64f;
                float xOffset  = positions.get(i).x_offset() / 64f;
                float yOffset  = positions.get(i).y_offset() / 64f;
                shaped.setGlyph(i, glyphId, xAdvance, yAdvance, xOffset, yOffset);
            }

            HarfBuzz.hb_buffer_destroy(buffer);
            return shaped;
        }
    }

    public void drawText(ShapedText text, float startX, float startY) {

        float x = startX;
        float y = startY;

        for (int i = 0; i < text.glyphCount; i++) {
            GlyphData glyphData = text.glyphs[i];

            //TODO Draw
            Log.info("Drawing glyph " + glyphData.glyphId + " at " + x + glyphData.xOffset + ", " + y + glyphData.yOffset);

            x += glyphData.xAdvance;
            y += glyphData.yAdvance;
        }

    }

    public void cleanup() {
        HarfBuzz.hb_font_destroy(font);
        FreeType.FT_Done_Face(face);
        FreeType.FT_Done_FreeType(freeType);
    }

    public static class ShapedText {
        final int glyphCount;
        final GlyphData[] glyphs;

        ShapedText(int glyphCount) {
            this.glyphCount = glyphCount;
            this.glyphs = new GlyphData[glyphCount];
        }

        void setGlyph(int index, int glyphId, float xAdv, float yAdv, float xOff, float yOff) {
            glyphs[index] = new GlyphData(glyphId, xAdv, yAdv, xOff, yOff);
        }
    }

    public static class GlyphData {
        final int glyphId;
        final float xAdvance, yAdvance;
        final float xOffset, yOffset;

        GlyphData(int glyphId, float xAdvance, float yAdvance, float xOffset, float yOffset) {
            this.glyphId = glyphId;
            this.xAdvance = xAdvance;
            this.yAdvance = yAdvance;
            this.xOffset = xOffset;
            this.yOffset = yOffset;
        }
    }

    public int getFontSize() {
        return fontSize;
    }

    public long getFreeType() {
        return freeType;
    }

    public long getFacePointer() {
        return facePointer;
    }

    public FT_Face getFace() {
        return face;
    }

    public long getFont() {
        return font;
    }
}
