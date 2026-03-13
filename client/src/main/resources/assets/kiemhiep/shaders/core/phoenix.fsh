#version 150

uniform sampler2D DiffuseSampler;
uniform float Progress;
uniform float Time;
uniform vec2 ScreenSize;

out vec4 fragColor;

void main() {
    vec2 uv = gl_FragCoord.xy / ScreenSize;

    // Phoenix wing spread với fire vortex
    float angle = atan(uv.y - 0.4, uv.x - 0.5);
    float dist = distance(uv, vec2(0.5, 0.4));

    float wing = sin(angle * 8.0 + Time * 4.0 + dist * 3.0) * 0.3 + 0.6;
    float vortex = 1.0 - smoothstep(0.0, 0.6, dist);

    // Orange-Yellow gradient
    vec3 phoenixColor = mix(vec3(1.0, 0.5, 0.1), vec3(1.0, 0.9, 0.3), wing);
    phoenixColor *= vortex;

    // Pulsing core
    float pulse = sin(Time * 8.0) * 0.2 + 1.0;

    fragColor = vec4(phoenixColor * pulse * Progress, 0.8 * Progress);
}
