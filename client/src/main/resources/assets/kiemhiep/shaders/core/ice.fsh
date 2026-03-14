#version 150

uniform sampler2D DiffuseSampler;
uniform float Progress;
uniform float Time;
uniform vec2 ScreenSize;

out vec4 fragColor;

void main() {
    vec2 uv = gl_FragCoord.xy / ScreenSize;

    // Pattern tinh thể cho hiệu ứng LUST
    float noise = sin(uv.x * 8.0 + Time * 3.0) * cos(uv.y * 8.0 + Time * 2.0) * 0.5 + 0.5;

    // Gradient tinh thể: xanh lam -> cyan -> trắng
    vec3 iceColor = mix(vec3(0.2, 0.5, 1.0), vec3(0.5, 0.8, 1.0), noise);
    iceColor = mix(iceColor, vec3(1.0, 1.0, 1.0), Progress);

    // Hiệu ứng shimmer
    float shimmer = sin(Time * 15.0) * 0.2 + 1.0;

    fragColor = vec4(iceColor * shimmer, 0.5 * Progress);
}
