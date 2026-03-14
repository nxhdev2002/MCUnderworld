#version 150

uniform sampler2D DiffuseSampler;
uniform float Progress;
uniform float Time;
uniform vec2 ScreenSize;

out vec4 fragColor;

void main() {
    vec2 uv = gl_FragCoord.xy / ScreenSize;

    // Firetrail từ trên xuống với gradient
    float fireHeight = uv.y * 2.0;
    float noise = sin(uv.x * 8.0 + Time * 6.0) * 0.3 + 0.5;

    // Yellow -> Orange -> Red gradient
    vec3 flameColor = mix(vec3(1.0, 0.9, 0.2), vec3(1.0, 0.5, 0.0), noise);
    flameColor = mix(flameColor, vec3(1.0, 0.2, 0.0), Progress);

    // Pulse effect
    float pulse = sin(Time * 12.0) * 0.3 + 1.0;

    fragColor = vec4(flameColor * pulse * Progress, 0.5 * Progress);
}
