package com.terminalvelocitycabbage.engine.client.renderer.materials;

import com.terminalvelocitycabbage.engine.client.ClientBase;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.filesystem.resources.Resource;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceType;
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

            //Load the image from the resource manager to a bytebuffer and error if it doesn't work
            ByteBuffer imageBuffer = stbi_load_from_memory(textureResource.asByteBuffer(), w, h, comp, 0);
            if (imageBuffer == null) Log.crash("Error loading texture: " + textureIdentifier + "\n" + stbi_failure_reason());

            //Assign the attributes of this texture for verification
            assert imageBuffer != null;
            var width = w.get(0);
            var height = h.get(0);
            var components = comp.get(0);

            //Textures need to be in a size multiple of 2, so make sure this one is
            if (width % 2 != 0 || height % 2 != 0) {
                Log.crash("Textures must be a multiple of 2 in both dimensions, texture " + textureIdentifier + "'s dimensions are: " + width + "px x " + height + "px");
            }

            //Generate the texture so it's accessible to opengl
            generateTexture(width, height, components, imageBuffer);

            //free the buffer data because we don't need it anymore
            stbi_image_free(imageBuffer);
        }
    }

    /**
     * Binds this texture for rendering
     */
    public void bind() {
        glBindTexture(GL_TEXTURE_2D, textureID);
    }

    /**
     * deletes this texture from opengl when we're done with it
     */
    public void cleanup() {
        glDeleteTextures(textureID);
    }

    /**
     * Generate this texture's context in opengl
     *
     * @param width the width of the texture we're generating
     * @param height the height of the texture
     * @param components the number of components that the pixels have (not yet used) //todo
     * @param buffer the buffer data for this texture
     */
    private void generateTexture(int width, int height, int components, ByteBuffer buffer) {
        //get the location that this texture will be bound to
        textureID = glGenTextures();

        //Error if it couldn't generate a texture
        if (textureID == 0) Log.error("generated texture: " + textureIdentifier + " resulted in an ID of: " + textureID);

        //Do all the OpenGL stuff we gotta do
        glBindTexture(GL_TEXTURE_2D, textureID);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 2 - (width & 1));
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        glGenerateMipmap(GL_TEXTURE_2D);
    }

}
