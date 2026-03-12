#version 150

uniform sampler2D DiffuseSampler;
uniform float Progress;
uniform float Time;
uniform vec2 ScreenSize;

out vec4 fragColor;

void main() {
    vec2 uv = gl_FragCoord.xy / ScreenSize;

    // Wave pattern cho hiệu ứng rung火焰
    float noise = sin(uv.x * 10.0 + Time * 5.0) * cos(uv.y * 10.0 + Time * 3.0) * 0.5 + 0.5;

    // Gradient flame: vàng -> cam -> đỏ
    vec3 flameColor = mix(vec3(1.0, 0.8, 0.0), vec3(1.0, 0.5, 0.0), noise);
    flameColor = mix(flameColor, vec3(1.0, 0.0, 0.0), Progress);

    // Hiệu ứng pulse (nhấp nháy)
    float pulse = sin(Time * 10.0) * 0.3 + 1.0;

    fragColor = vec4(flameColor * pulse, 0.6 * Progress);
}
