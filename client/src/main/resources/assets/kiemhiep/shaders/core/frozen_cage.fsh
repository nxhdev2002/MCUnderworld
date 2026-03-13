#version 150

uniform sampler2D DiffuseSampler;
uniform float Progress;
uniform float Time;
uniform vec2 ScreenSize;

out vec4 fragColor;

void main() {
    vec2 uv = gl_FragCoord.xy / ScreenSize;

    // Frozen cage với ice cage pattern
    float noise = sin(uv.x * 10.0 + Time * 3.0) * cos(uv.y * 10.0 + Time * 3.0) * 0.3 + 0.7;
    float dist = distance(uv, vec2(0.5, 0.5));

    // Circular cage pattern with rotating effect
    float angle = atan(uv.y - 0.5, uv.x - 0.5);
    float cage = 1.0 - smoothstep(0.3, 0.35, abs(dist - 0.25));
    cage *= 1.0 - smoothstep(0.45, 0.5, dist);

    // Ice shimmer effect
    float shimmer = noise * cage * (sin(angle * 4.0 + Time * 5.0) * 0.5 + 0.5);

    // Cyan-blue ice gradient
    vec3 iceColor = mix(vec3(0.5, 0.8, 1.0), vec3(0.8, 1.0, 1.0), shimmer);
    iceColor *= cage;

    // Pulsing ice effect
    float pulse = sin(Time * 8.0) * 0.2 + 1.0;

    fragColor = vec4(iceColor * pulse * Progress, 0.7 * Progress);
}
