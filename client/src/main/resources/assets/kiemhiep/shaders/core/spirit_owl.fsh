#version 150

uniform sampler2D DiffuseSampler;
uniform float Progress;
uniform float Time;
uniform vec2 ScreenSize;

out vec4 fragColor;

void main() {
    vec2 uv = gl_FragCoord.xy / ScreenSize;

    // Wind swirl effect với mist trail
    float angle = atan(uv.y - 0.4, uv.x - 0.5);
    float dist = distance(uv, vec2(0.5, 0.4));

    float swirl = sin(angle * 10.0 + Time * 3.0 + dist * 5.0) * 0.3 + 0.6;
    float mist = 1.0 - smoothstep(0.0, 0.6, dist);

    // White with blue tint
    vec3 swirlColor = mix(vec3(0.8, 0.9, 1.0), vec3(1.0, 1.0, 1.0), swirl);
    swirlColor *= mist;

    // Shimmer
    float shimmer = sin(Time * 18.0) * 0.2 + 1.0;

    fragColor = vec4(swirlColor * shimmer * Progress, 0.5 * Progress);
}
