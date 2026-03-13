#version 150

uniform sampler2D DiffuseSampler;
uniform float Progress;
uniform float Time;
uniform vec2 ScreenSize;

out vec4 fragColor;

void main() {
    vec2 uv = gl_FragCoord.xy / ScreenSize;

    // Sand vortex với crab shell reveal
    float noise = sin(uv.x * 12.0 + Time * 5.0) * cos(uv.y * 10.0 + Time * 3.0) * 0.3 + 0.5;
    float dist = distance(uv, vec2(0.5, 0.4));

    float vortex = 1.0 - smoothstep(0.0, 0.5, dist);
    float shell = sin(uv.x * 6.0 + uv.y * 6.0 + Time * 2.0) * 0.3 + 0.6;

    // Brown sand gradient
    vec3 sandColor = mix(vec3(0.8, 0.6, 0.3), vec3(0.6, 0.4, 0.2), noise);
    sandColor *= vortex * shell;

    // Pulsing
    float pulse = sin(Time * 10.0) * 0.2 + 1.0;

    fragColor = vec4(sandColor * pulse * Progress, 0.6 * Progress);
}
