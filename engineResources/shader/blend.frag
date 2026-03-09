#version 330 core

in vec2 fTexCoords;

uniform sampler2D sceneTex;

out vec4 color;

void main() {
	vec3 scene = texture(sceneTex, fTexCoords).rgb;

	color = vec4(scene, 1);
}
