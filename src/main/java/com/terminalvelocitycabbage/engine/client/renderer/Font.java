package com.terminalvelocitycabbage.engine.client.renderer;

import com.terminalvelocitycabbage.engine.client.ClientBase;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceCategory;
import com.terminalvelocitycabbage.engine.registry.Identifiable;
import com.terminalvelocitycabbage.engine.registry.Identifier;

import java.nio.ByteBuffer;
import java.util.Objects;

import static org.lwjgl.nanovg.NanoVG.nvgCreateFontMem;

public final class Font implements Identifiable {

    private final Identifier resourceIdentifier;
    private int fontId;

    public Font(Identifier resourceIdentifier) {
        this.resourceIdentifier = resourceIdentifier;
        this.fontId = -1;
    }

    @Override
    public Identifier getIdentifier() {
        return resourceIdentifier;
    }

    public int getOrLoadFont(long nvg) {

        if (fontId != -1) {
            return fontId;
        }

        var font = ClientBase.getInstance().getFontRegistry().get(resourceIdentifier);
        if (font == null) {
            Log.error("Font not found in registry: " + resourceIdentifier);
            return -1;
        }

        var resource = ClientBase.getInstance().getFileSystem().getResource(ResourceCategory.FONT, font.resourceIdentifier());
        if (resource == null) {
            Log.error("Font resource not found: " + font.resourceIdentifier());
            return -1;
        }

        ByteBuffer data = resource.asByteBuffer(true);
        int handle = nvgCreateFontMem(nvg, resourceIdentifier.toString(), data, false);
        if (handle == -1) {
            Log.error("Failed to load font into NanoVG: " + resourceIdentifier);
        } else {
            fontId = handle;
        }
        return handle;
    }

    public Identifier resourceIdentifier() {
        return resourceIdentifier;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Font) obj;
        return Objects.equals(this.resourceIdentifier, that.resourceIdentifier) &&
                Objects.equals(this.fontId, that.fontId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resourceIdentifier, fontId);
    }

    @Override
    public String toString() {
        return "Font[" +
                "resourceIdentifier=" + resourceIdentifier + ", " +
                "fontId=" + fontId + ']';
    }

}
