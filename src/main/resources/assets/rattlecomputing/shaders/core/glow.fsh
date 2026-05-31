#version 150

in vec2 uv;
out vec4 fragColor;

uniform vec3 LedColor;
uniform float Intensity;

void main() {
    float dist = distance(uv, vec2(0.5));
    float glow = 1.0 - smoothstep(0.2, 0.5, dist);
    glow *= Intensity;
    fragColor = vec4(LedColor * glow, glow);
}