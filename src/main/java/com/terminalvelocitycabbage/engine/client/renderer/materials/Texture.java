package com.terminalvelocitycabbage.engine.client.renderer.materials;

import com.terminalvelocitycabbage.engine.client.ClientBase;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.filesystem.resources.Resource;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceType;
import com.terminalvelocitycabbage.engine.filesystem.resources.types.JarResource;
import com.terminalvelocitycabbage.engine.filesystem.resources.types.URLResource;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.*;

public class Texture {

    int textureID;
    Identifier textureIdentifier;

    public Texture(Identifier textureIdentifier) {
        this(textureIdentifier, ClientBase.getInstance().getFileSystem().getResource(ResourceType.TEXTURE, textureIdentifier));
    }

    public Texture(Identifier textureIdentifier, Resource textureResource) {
        this.textureIdentifier = textureIdentifier;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);

            ByteBuffer imageBuffer = stbi_load_from_memory(textureResource.asByteBuffer(true), w, h, comp, 4);
            if (imageBuffer == null) Log.crash("Error loading texture: " + textureIdentifier + "\n" + stbi_failure_reason());

            assert imageBuffer != null;
            generateTexture(w.get(0), h.get(0), imageBuffer);

            stbi_image_free(imageBuffer);
        }
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, textureID);
    }

    public void cleanup() {
        glDeleteTextures(textureID);
    }

    private void generateTexture(int width, int height, ByteBuffer buffer) {
        textureID = glGenTextures();

        glBindTexture(GL_TEXTURE_2D, textureID);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        glGenerateMipmap(GL_TEXTURE_2D);
    }

}
