#version 150

uniform sampler2D DiffuseSampler;
uniform float Progress;
uniform float Time;
uniform vec2 ScreenSize;

out vec4 fragColor;

void main() {
    vec2 uv = gl_FragCoord.xy / ScreenSize;

    // Arrow rainfall với metal shine
    float noise = sin(uv.y * 15.0 + Time * 8.0) * cos(uv.x * 10.0 + Time * 5.0) * 0.3 + 0.6;
    float dist = distance(uv, vec2(0.5, 0.4));

    float rain = 1.0 - smoothstep(0.0, 0.5, dist);
    float shine = noise * rain;

    // White metal gradient
    vec3 arrowColor = mix(vec3(0.8, 0.8, 0.8), vec3(1.0, 1.0, 1.0), shine);
    arrowColor *= rain;

    // Pulsing
    float pulse = sin(Time * 10.0) * 0.2 + 1.0;

    fragColor = vec4(arrowColor * pulse * Progress, 0.6 * Progress);
}
