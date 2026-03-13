#version 150

uniform sampler2D DiffuseSampler;
uniform float Progress;
uniform float Time;
uniform vec2 ScreenSize;

out vec4 fragColor;

void main() {
    vec2 uv = gl_FragCoord.xy / ScreenSize;

    // Energy shield với rotating护盾
    float angle = atan(uv.y - 0.4, uv.x - 0.5);
    float dist = distance(uv, vec2(0.5, 0.4));

    float shield = sin(angle * 10.0 + Time * 5.0) * 0.3 + 0.7;
    float barrier = 1.0 - smoothstep(0.0, 0.5, dist);

    // Light blue gradient
    vec3 shieldColor = mix(vec3(0.8, 0.9, 1.0), vec3(0.5, 0.7, 1.0), shield);
    shieldColor *= barrier;

    // Pulsing
    float pulse = sin(Time * 12.0) * 0.2 + 1.0;

    fragColor = vec4(shieldColor * pulse * Progress, 0.8 * Progress);
}
