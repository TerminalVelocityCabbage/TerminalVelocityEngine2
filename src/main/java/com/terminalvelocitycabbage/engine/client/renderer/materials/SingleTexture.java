package com.terminalvelocitycabbage.engine.client.renderer.materials;

import com.terminalvelocitycabbage.engine.client.ClientBase;
import com.terminalvelocitycabbage.engine.filesystem.resources.Resource;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceCategory;
import com.terminalvelocitycabbage.engine.registry.Identifier;

public class SingleTexture extends Texture {

    public SingleTexture(Identifier textureIdentifier) {
        this(textureIdentifier, ClientBase.getInstance().getFileSystem().getResource(ResourceCategory.TEXTURE, textureIdentifier));
    }

    public SingleTexture(Identifier textureIdentifier, Resource textureResource) {
        var textureData = Data.fromResource(textureIdentifier, textureResource);
        generateOpenGLTexture(textureData.width(), textureData.height(), textureData.components(), textureData.imageBuffer());
        textureData.free();
    }
}
