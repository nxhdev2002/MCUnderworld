#version 150

uniform sampler2D DiffuseSampler;
uniform float Progress;
uniform float Time;
uniform vec2 ScreenSize;

out vec4 fragColor;

void main() {
    vec2 uv = gl_FragCoord.xy / ScreenSize;

    // Pattern khí độc
    float noise = sin(uv.x * 10.0 + Time * 2.0) * cos(uv.y * 8.0 + Time * 3.0) * 0.5 + 0.5;

    // Gradient độc: xanh -> tím -> đen
    vec3 poisonColor = mix(vec3(0.2, 0.8, 0.2), vec3(0.5, 0.2, 0.5), noise);
    poisonColor = mix(poisonColor, vec3(0.1, 0.05, 0.1), Progress);

    // Hiệu ứng mây độc
    float cloud = sin(uv.x * 6.0 + Time * 4.0) * 0.3;
    vec3 finalColor = poisonColor + vec3(cloud) * 0.2;

    fragColor = vec4(finalColor, 0.5 * Progress);
}
