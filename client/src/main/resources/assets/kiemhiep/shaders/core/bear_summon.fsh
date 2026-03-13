#version 150

uniform sampler2D DiffuseSampler;
uniform float Progress;
uniform float Time;
uniform vec2 ScreenSize;

out vec4 fragColor;

void main() {
    vec2 uv = gl_FragCoord.xy / ScreenSize;

    // Earth pillars với spirit pattern
    float angle = atan(uv.y - 0.4, uv.x - 0.5);
    float dist = distance(uv, vec2(0.5, 0.4));

    float pillar = 1.0 - smoothstep(0.0, 0.5, dist);
    float noise = sin(angle * 8.0 + Time * 2.0) * 0.3 + 0.6;

    // Dark Earth gradient
    vec3 earthColor = mix(vec3(0.5, 0.3, 0.1), vec3(0.3, 0.2, 0.1), noise);
    earthColor *= pillar;

    // Pulsing spirit
    float pulse = sin(Time * 6.0) * 0.2 + 1.0;

    fragColor = vec4(earthColor * pulse * Progress, 0.7 * Progress);
}
