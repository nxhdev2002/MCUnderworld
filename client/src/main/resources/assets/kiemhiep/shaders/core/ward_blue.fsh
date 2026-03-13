#version 150

uniform sampler2D DiffuseSampler;
uniform float Progress;
uniform float Time;
uniform vec2 ScreenSize;

out vec4 fragColor;

void main() {
    vec2 uv = gl_FragCoord.xy / ScreenSize;

    // Crystal forming effect với blue shimmer
    float noise = sin(uv.x * 10.0 + Time * 4.0) * cos(uv.y * 8.0 + Time * 3.0) * 0.3 + 0.5;
    float dist = distance(uv, vec2(0.5, 0.4));

    // Blue crystal gradient
    vec3 crystalColor = mix(vec3(0.2, 0.5, 1.0), vec3(0.4, 0.7, 1.0), noise);
    crystalColor *= 1.0 - smoothstep(0.0, 0.5, dist);

    // Pulsing effect
    float pulse = sin(Time * 15.0) * 0.2 + 1.0;

    fragColor = vec4(crystalColor * pulse * Progress, 0.7 * Progress);
}
