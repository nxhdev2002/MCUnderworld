#version 150

uniform sampler2D DiffuseSampler;
uniform float Progress;
uniform float Time;
uniform vec2 ScreenSize;

out vec4 fragColor;

void main() {
    vec2 uv = gl_FragCoord.xy / ScreenSize;

    // Starlight rain từ trên xuống
    float rain = sin(uv.y * 10.0 + Time * 5.0) * cos(uv.x * 8.0 + Time * 3.0) * 0.3 + 0.7;
    float dist = distance(uv, vec2(0.5, 0.4));

    // Cyan -> White gradient
    vec3 starColor = mix(vec3(0.4, 0.8, 1.0), vec3(1.0, 1.0, 1.0), rain);
    starColor *= 1.0 - smoothstep(0.0, 0.5, dist);

    // Shimmer effect
    float shimmer = sin(Time * 20.0) * 0.2 + 1.0;

    fragColor = vec4(starColor * shimmer * Progress, 0.6 * Progress);
}
