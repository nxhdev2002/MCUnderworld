#version 150

uniform sampler2D DiffuseSampler;
uniform float Progress;
uniform float Time;
uniform vec2 ScreenSize;

out vec4 fragColor;

void main() {
    vec2 uv = gl_FragCoord.xy / ScreenSize;

    // Flame sentinel với rotating orange ring
    float angle = atan(uv.y - 0.4, uv.x - 0.5);
    float dist = distance(uv, vec2(0.5, 0.4));

    float ring = sin(angle * 6.0 + Time * 5.0) * 0.3 + 0.7;
    float base = 1.0 - smoothstep(0.0, 0.4, dist);

    // Orange-Red flame
    vec3 flameColor = mix(vec3(1.0, 0.4, 0.1), vec3(1.0, 0.7, 0.2), base);
    flameColor *= ring;

    // Pulsing
    float pulse = sin(Time * 10.0) * 0.2 + 1.0;

    fragColor = vec4(flameColor * pulse * Progress, 0.8 * Progress);
}
