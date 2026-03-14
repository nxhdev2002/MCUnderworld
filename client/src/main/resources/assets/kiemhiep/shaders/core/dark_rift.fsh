#version 150

uniform sampler2D DiffuseSampler;
uniform float Progress;
uniform float Time;
uniform vec2 ScreenSize;

out vec4 fragColor;

void main() {
    vec2 uv = gl_FragCoord.xy / ScreenSize;

    // Void pull với purple distortion
    float angle = atan(uv.y - 0.4, uv.x - 0.5);
    float dist = distance(uv, vec2(0.5, 0.4));

    float pull = sin(angle * 8.0 + Time * 4.0 + dist * 5.0) * 0.3 + 0.6;
    float core = 1.0 - smoothstep(0.0, 0.4, dist);

    // Purple gradient
    vec3 pullColor = mix(vec3(0.3, 0.0, 0.5), vec3(0.0, 0.0, 0.1), pull);
    pullColor *= core;

    // Pulsing
    float pulse = sin(Time * 8.0) * 0.2 + 1.0;

    fragColor = vec4(pullColor * pulse * Progress, 0.8 * Progress);
}
