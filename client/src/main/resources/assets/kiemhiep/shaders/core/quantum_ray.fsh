#version 150

uniform sampler2D DiffuseSampler;
uniform float Progress;
uniform float Time;
uniform vec2 ScreenSize;

out vec4 fragColor;

void main() {
    vec2 uv = gl_FragCoord.xy / ScreenSize;

    // Electric beam với neon glow
    float noise = sin(uv.x * 10.0 + Time * 6.0) * cos(uv.y * 8.0 + Time * 4.0) * 0.3 + 0.6;
    float dist = distance(uv, vec2(0.5, 0.4));

    float beam = 1.0 - smoothstep(0.0, 0.4, dist);
    float neon = noise * beam;

    // Cyan gradient
    vec3 electricColor = mix(vec3(0.2, 0.8, 1.0), vec3(0.0, 1.0, 1.0), neon);
    electricColor *= beam;

    // Pulsing
    float pulse = sin(Time * 15.0) * 0.2 + 1.0;

    fragColor = vec4(electricColor * pulse * Progress, 0.7 * Progress);
}
