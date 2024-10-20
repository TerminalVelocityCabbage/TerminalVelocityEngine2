package com.terminalvelocitycabbage.engine.client.renderer.shader;

import com.terminalvelocitycabbage.engine.client.ClientBase;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceCategory;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.util.StringUtils;

import java.util.Objects;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;
import static org.lwjgl.opengl.GL40.GL_TESS_CONTROL_SHADER;
import static org.lwjgl.opengl.GL40.GL_TESS_EVALUATION_SHADER;

public class Shader {

    final Type shaderType;
    final Identifier shaderSourceIdentifier;

    /**
     * @param shaderType The type of shader that this is
     * @param shaderSourceIdentifier The identifer that is used to find this shader
     */
    public Shader(Type shaderType, Identifier shaderSourceIdentifier) {
        this.shaderType = shaderType;
        this.shaderSourceIdentifier = shaderSourceIdentifier;
    }

    /**
     * Replaces all #include "blah:blah" with the source of the resource defined by that identifier
     *
     * @param source the source for this shader to be parsing
     * @return a modified version of the source with inclusions included from this filesystem
     */
    private String parseInclusions(String source) {
        //Get the identifier of the shader that the parent shader is trying to import from its include definition
        String importName = StringUtils.getStringBetween(source, "#include \"", "\";");
        Identifier importIdentifier = Identifier.of(Objects.requireNonNull(importName));

        //Try to get the resource for the shader trying to be included
        var resource = ClientBase.getInstance().getFileSystem().getResource(ResourceCategory.SHADER, importIdentifier);

        //Attempt to insert the included shader source in place of the requested.
        return source.replace("#include \"" + importName + "\";", resource.asString());
    }

    /**
     * @return The shader id for this shader once created
     */
    public int create() {

        //Create this shader
        int shaderID = glCreateShader(shaderType.getGLType());
        if (shaderID == 0) Log.crash("Error creating shader of type: " + shaderType.name());

        //Get this shader from its resource
        String shaderString = ClientBase.getInstance().getFileSystem().getResource(ResourceCategory.SHADER, shaderSourceIdentifier).asString();
        //Compile shader from include definitions
        while (shaderString.contains("#include")) {
            shaderString = parseInclusions(shaderString);
        }
        //Attach these sources to the shader and compile it
        glShaderSource(shaderID, shaderString);
        glCompileShader(shaderID);

        //Make sure it compiled
        if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL_FALSE) {
            String error = glGetShaderInfoLog(shaderID);
            //Disabled beflow because it's pretty inconsistent with where it actually errors
//            int lineNum = Integer.parseInt(error.split("\\(")[1].split("\\)")[0]) - 1;
//            Log.crash("Could not compile shader: " + error + "\n Error on line " + lineNum + ": " + shaderString.split("\n")[lineNum]);
            Log.crash("Could not compile shader: " + error);
        }

        //Return it to be attached by the program
        return shaderID;
    }

    /**
     * The types of shader that TVE supports
     */
    public enum Type {
        VERTEX(GL_VERTEX_SHADER),
        FRAGMENT(GL_FRAGMENT_SHADER),
        GEOMETRY(GL_GEOMETRY_SHADER),
        TESS_CONTROL(GL_TESS_CONTROL_SHADER),
        TESS_EVAL(GL_TESS_EVALUATION_SHADER);

        private final int glCode;

        Type(int glCode) {
            this.glCode = glCode;
        }

        /**
         * @return The opengl number associated with this type
         */
        public int getGLType() {
            return this.glCode;
        }
    }

}
