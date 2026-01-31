#version 330

in vec2 outTextureCoord;
out vec4 fragColor;

uniform sampler2D textureSampler;

void main()
{
    vec4 color = texture(textureSampler, outTextureCoord);
    if (color.a == 0) {
        discard;
    } else {
        fragColor = color;
    }

}