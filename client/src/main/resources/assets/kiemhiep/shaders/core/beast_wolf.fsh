#version 150

uniform sampler2D DiffuseSampler;
uniform float Progress;
uniform float Time;
uniform vec2 ScreenSize;

out vec4 fragColor;

void main() {
    vec2 uv = gl_FragCoord.xy / ScreenSize;

    // Fire claw slash với orange-red gradient
    float angle = atan(uv.y - 0.4, uv.x - 0.5);
    float dist = distance(uv, vec2(0.5, 0.4));

    float slash = sin(angle * 4.0 + Time * 2.0) * 0.3 + 0.7;
    float base = 1.0 - smoothstep(0.0, 0.5, dist);

    // Orange-Red gradient
    vec3 clawColor = mix(vec3(1.0, 0.5, 0.2), vec3(1.0, 0.2, 0.0), base);
    clawColor *= slash;

    // Pulsing
    float pulse = sin(Time * 15.0) * 0.2 + 1.0;

    fragColor = vec4(clawColor * pulse * Progress, 0.7 * Progress);
}
