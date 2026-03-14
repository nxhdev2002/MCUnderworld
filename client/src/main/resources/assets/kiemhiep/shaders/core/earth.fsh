#version 150

uniform sampler2D DiffuseSampler;
uniform float Progress;
uniform float Time;
uniform vec2 ScreenSize;

out vec4 fragColor;

void main() {
    vec2 uv = gl_FragCoord.xy / ScreenSize;

    // Pattern đất cho hiệu ứng rung
    float noise = sin(uv.x * 12.0 + Time * 4.0) * sin(uv.y * 12.0 + Time * 3.0) * 0.5 + 0.5;

    // Gradient đất: nâu -> xanh đất -> vàng
    vec3 earthColor = mix(vec3(0.6, 0.4, 0.2), vec3(0.3, 0.5, 0.2), noise);
    earthColor = mix(earthColor, vec3(0.7, 0.5, 0.3), Progress);

    // Hiệu ứng rung đất
    float shake = sin(Time * 20.0) * 0.1;
    vec3 finalColor = earthColor + vec3(shake);

    fragColor = vec4(finalColor, 0.5 * Progress);
}
