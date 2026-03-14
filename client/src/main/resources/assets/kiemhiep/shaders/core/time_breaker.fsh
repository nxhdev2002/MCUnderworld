#version 150

uniform sampler2D DiffuseSampler;
uniform float Progress;
uniform float Time;
uniform vec2 ScreenSize;

out vec4 fragColor;

void main() {
    vec2 uv = gl_FragCoord.xy / ScreenSize;

    // Time warp với blue streak
    float noise = sin(uv.x * 15.0 + Time * 8.0) * cos(uv.y * 10.0 + Time * 5.0) * 0.3 + 0.5;
    float dist = distance(uv, vec2(0.5, 0.4));

    float streak = 1.0 - smoothstep(0.0, 0.5, dist);
    float warp = noise * streak;

    // Cyan-Yellow time warp
    vec3 timeColor = mix(vec3(0.0, 1.0, 1.0), vec3(1.0, 1.0, 0.0), noise);
    timeColor *= streak;

    // Pulsing
    float pulse = sin(Time * 20.0) * 0.2 + 1.0;

    fragColor = vec4(timeColor * pulse * Progress, 0.6 * Progress);
}
