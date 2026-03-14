#version 150

uniform sampler2D DiffuseSampler;
uniform float Progress;
uniform float Time;
uniform vec2 ScreenSize;

out vec4 fragColor;

void main() {
    vec2 uv = gl_FragCoord.xy / ScreenSize;

    // Light spear với glowing trail
    float noise = sin(uv.x * 8.0 + Time * 4.0) * cos(uv.y * 12.0 + Time * 3.0) * 0.3 + 0.6;
    float dist = distance(uv, vec2(0.5, 0.4));

    float spear = 1.0 - smoothstep(0.0, 0.4, dist);
    float glow = noise * spear;

    // White light gradient
    vec3 lightColor = mix(vec3(0.9, 0.9, 1.0), vec3(1.0, 1.0, 1.0), glow);
    lightColor *= spear;

    // Pulsing
    float pulse = sin(Time * 20.0) * 0.2 + 1.0;

    fragColor = vec4(lightColor * pulse * Progress, 0.8 * Progress);
}
