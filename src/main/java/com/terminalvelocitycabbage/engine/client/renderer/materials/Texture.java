package com.terminalvelocitycabbage.engine.client.renderer.materials;

import com.terminalvelocitycabbage.engine.client.ClientBase;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.filesystem.resources.Resource;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceCategory;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;

public class Texture implements SingleTexture {

    int textureID;
    int width;
    int height;

    public Texture(Identifier textureIdentifier) {
        this(textureIdentifier, ClientBase.getInstance().getFileSystem().getResource(ResourceCategory.TEXTURE, textureIdentifier));
    }

    public Texture(Identifier textureIdentifier, Resource textureResource) {

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);

            //Load the image from the resource manager to a bytebuffer and error if it doesn't work
            ByteBuffer imageBuffer = stbi_load_from_memory(textureResource.asByteBuffer(), w, h, comp, 0);
            if (imageBuffer == null) Log.crash("Error loading texture: " + textureIdentifier + "\n" + stbi_failure_reason());

            //Assign the attributes of this texture for verification
            assert imageBuffer != null;
            width = w.get(0);
            height = h.get(0);
            var components = comp.get(0);

            //Textures need to be in a size multiple of 2, so make sure this one is
            if (width % 2 != 0 || height % 2 != 0) {
                Log.crash("Textures must be a multiple of 2 in both dimensions, texture " + textureIdentifier + "'s dimensions are: " + width + "px x " + height + "px");
            }

            //Generate the texture so it's accessible to opengl
            this.textureID = generateOpenGLTexture(textureIdentifier, width, height, components, imageBuffer);

            //free the buffer data because we don't need it anymore
            stbi_image_free(imageBuffer);
        }
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
}
