package com.terminalvelocitycabbage.engine.client.renderer.materials;

import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.filesystem.resources.Resource;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.*;

public abstract class SingleTexture {

    int textureID;
    int width;
    int height;

    record Data(int width, int height, int components, ByteBuffer imageBuffer) implements Comparable<Data> {

        static Data fromResource(Identifier textureIdentifier, Resource textureResource) {
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

                return new Data(width, height, components, imageBuffer);
            }
        }

        void free() {
            stbi_image_free(imageBuffer);
        }

        @Override
        public int compareTo(Data o) {
            return (this.width * this.height) - (o.width * o.height);
        }
    }

    int generateOpenGLTexture(int width, int height, int components, ByteBuffer imageBuffer) {
        //get the location that this texture will be bound to
        int textureID = glGenTextures();

        //Error if it couldn't generate a texture
        if (textureID == 0) Log.crash("texture generation failed");

        //Do all the OpenGL stuff we gotta do
        glBindTexture(GL_TEXTURE_2D, textureID);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 2 - (width & 1));
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, imageBuffer);
        glGenerateMipmap(GL_TEXTURE_2D);

        return textureID;
    }

    /**
     * Binds this texture for rendering
     */
    void bind() {
        glBindTexture(GL_TEXTURE_2D, getTextureID());
    }

    /**
     * deletes this texture from opengl when we're done with it
     */
    void cleanup() {
        glDeleteTextures(getTextureID());
    }

    public int getTextureID() {
        return textureID;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
