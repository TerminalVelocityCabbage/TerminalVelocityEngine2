package com.terminalvelocitycabbage.engine.client.renderer.materials;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL12.glTexSubImage3D;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_2D_ARRAY;
import static org.lwjgl.opengl.GL42.glTexStorage3D;

public class TextureArray {

    int textureId;

    //This should probably be in the constructor and the array should have a builder? depends on how we want to stream textures
    //Possibly have some field on the texture to "always include on init" or this size can be set by the user for number of streamed in textures?
    //Not sure how to resize it if we overflow
    public void init(int mipLevels, int width, int height, int numLayers) {
        textureId = glGenTextures();
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D_ARRAY, textureId);

        glTexStorage3D(GL_TEXTURE_2D_ARRAY, mipLevels, GL_RGB, width, height, numLayers);

        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D_ARRAY, textureId);
    }

    public void uploadTexture(Texture texture, int layer) {
        //glTexSubImage3D(GL_TEXTURE_2D_ARRAY, 0, 0, 0, layer, GL_RGB, GL_UNSIGNED_BYTE, texture.getTextureData());
    }

    public void updateTextures() {
        //TODO should loop through all textures in the array and update the ones mark as having changes
    }

}
