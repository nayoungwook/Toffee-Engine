#version 330 core

uniform vec4 uColor;

out vec4 color;

void main() {
	color = vec4(uColor.rgb * uColor.a, uColor.a);
}
