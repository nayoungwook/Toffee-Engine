#version 330 core

uniform sampler2D uTexture;

in vec2 fTexCoords;

out vec4 color;

void main() {
	vec4 texColor = texture(uTexture, fTexCoords);

	bool drawOutline = false;

	float uOutlineThickness = 0.02f;

	float alpha = texColor.a;
	float left =
			texture(uTexture, fTexCoords + vec2(-uOutlineThickness, 0)).a;
	float right =
			texture(uTexture, fTexCoords + vec2(uOutlineThickness, 0)).a;
	float up = texture(uTexture, fTexCoords + vec2(0, uOutlineThickness)).a;
	float down =
			texture(uTexture, fTexCoords + vec2(0, -uOutlineThickness)).a;

	drawOutline = (alpha > 0.1)
			&& (left < 0.1 || right < 0.1 || up < 0.1 || down < 0.1)
			&& !(left < 0.1 && right < 0.1 && up < 0.1 && down < 0.1);

	if (drawOutline) {
		color = vec4(1, 0, 0, 1.0); // 외곽선 색상 적용
	} else {
		color = texColor;
	}
}
