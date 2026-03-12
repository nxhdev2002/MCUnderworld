#version 150

uniform sampler2D DiffuseSampler;
uniform float Progress;
uniform float Time;
uniform vec2 ScreenSize;

out vec4 fragColor;

void main() {
    vec2 uv = gl_FragCoord.xy / ScreenSize;

    // Pattern gió xoáy
    vec2 center = vec2(0.5, 0.5);
    vec2 diff = uv - center;
    float angle = atan(diff.y, diff.x);
    float dist = length(diff);

    float vortex = sin(angle * 5.0 - Time * 3.0) * 0.5 + 0.5;

    // Gradient gió: xanh nhạt -> xám
    vec3 windColor = mix(vec3(0.7, 0.8, 1.0), vec3(0.5, 0.5, 0.6), vortex);
    windColor = mix(windColor, vec3(0.4, 0.4, 0.5), dist * 2.0);

    // Hiệu ứng xoáy gió
    float swirl = sin(angle * 8.0 - Time * 5.0) * 0.3;
    vec3 finalColor = windColor + vec3(swirl) * 0.2;

    fragColor = vec4(finalColor, 0.3 * Progress);
}
