#version 330 core

uniform sampler2D uTexture;
uniform sampler2D another;

uniform vec2 uPoint = vec2(0, 0);
uniform vec2 uDisplaySize;

in vec2 fTexCoords;

out vec4 color;

vec4 applyLight(vec4 tex, vec3 light, float range) {
	float distance = length(uPoint - gl_FragCoord.xy + uDisplaySize / 2) / 500;
	float attenuation = 1 / distance / 10 * range;
	vec4 finalLight = vec4(attenuation, attenuation, attenuation,
			pow(attenuation, 3));

	return clamp(finalLight, vec4(0, 0, 0, 1), vec4(0.6, 0.6, 0.6, 1)) * tex * 2
			* vec4(light, 1);
}

void main() {
	vec2 uv = fTexCoords;
	vec4 tex = texture(uTexture, uv) + texture(another, uv);

	color = tex;
}
