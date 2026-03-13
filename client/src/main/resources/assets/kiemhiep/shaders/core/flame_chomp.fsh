#version 150

uniform sampler2D DiffuseSampler;
uniform float Progress;
uniform float Time;
uniform vec2 ScreenSize;

out vec4 fragColor;

void main() {
    vec2 uv = gl_FragCoord.xy / ScreenSize;

    // Fire jaws snapping với fire vortex
    float angle = atan(uv.y - 0.4, uv.x - 0.5);
    float dist = distance(uv, vec2(0.5, 0.4));

    float snap = sin(angle * 6.0 + Time * 8.0) * 0.3 + 0.7;
    float fire = 1.0 - smoothstep(0.0, 0.4, dist);

    // Fire gradient
    vec3 fireColor = mix(vec3(1.0, 0.4, 0.0), vec3(1.0, 0.0, 0.0), fire);
    fireColor *= snap;

    // Pulsing
    float pulse = sin(Time * 15.0) * 0.2 + 1.0;

    fragColor = vec4(fireColor * pulse * Progress, 0.7 * Progress);
}
