#version 150

uniform sampler2D DiffuseSampler;
uniform float Progress;
uniform float Time;
uniform vec2 ScreenSize;

out vec4 fragColor;

void main() {
    vec2 uv = gl_FragCoord.xy / ScreenSize;

    // Dark rift với purple energy core
    float angle = atan(uv.y - 0.4, uv.x - 0.5);
    float dist = distance(uv, vec2(0.5, 0.4));

    float rift = sin(angle * 12.0 + Time * 6.0 + dist * 8.0) * 0.3 + 0.5;
    float core = 1.0 - smoothstep(0.0, 0.4, dist);

    // Purple-Black gradient
    vec3 riftColor = mix(vec3(0.2, 0.0, 0.4), vec3(0.0, 0.0, 0.1), rift);
    riftColor *= core;

    // Pulsing core
    float pulse = sin(Time * 4.0) * 0.2 + 1.0;

    fragColor = vec4(riftColor * pulse * Progress, 0.9 * Progress);
}
