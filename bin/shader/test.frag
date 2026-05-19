#version 330 core

in vec2 fTexCoords;

out vec4 color;

void main()
{
    color = vec4(fTexCoords.x, fTexCoords.y, fTexCoords.x * fTexCoords.y, 1);
}
