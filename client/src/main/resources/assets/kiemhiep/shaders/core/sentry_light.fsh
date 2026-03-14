#version 150

uniform sampler2D DiffuseSampler;
uniform float Progress;
uniform float Time;
uniform vec2 ScreenSize;

out vec4 fragColor;

void main() {
    vec2 uv = gl_FragCoord.xy / ScreenSize;

    // Light rays rising từ trung tâm
    float dist = distance(uv, vec2(0.5, 0.3));
    float angle = atan(uv.y - 0.3, uv.x - 0.5);

    // Radial gradient từ center
    float light = 1.0 - smoothstep(0.0, 0.3, dist);
    light *= sin(angle * 8.0 + Time * 3.0) * 0.2 + 0.8;

    // White-Yellow glow
    vec3 lightColor = mix(vec3(1.0, 1.0, 1.0), vec3(1.0, 0.9, 0.5), light);

    fragColor = vec4(lightColor * Progress, 0.8 * Progress);
}
