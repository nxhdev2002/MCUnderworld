#version 150

uniform sampler2D DiffuseSampler;
uniform float Progress;
uniform float Time;
uniform vec2 ScreenSize;

out vec4 fragColor;

void main() {
    vec2 uv = gl_FragCoord.xy / ScreenSize;

    // Electric snake với looping電 pattern
    float noise = sin(uv.x * 8.0 + Time * 4.0) * cos(uv.y * 6.0 + Time * 3.0) * 0.3 + 0.7;
    float dist = distance(uv, vec2(0.5, 0.5));

    // Snake body pattern - S-shape curve
    float snakeY = 0.5 + sin(uv.x * 8.0 + Time * 6.0) * 0.3;
    float snakeDist = abs(uv.y - snakeY);
    float snake = 1.0 - smoothstep(0.02, 0.08, snakeDist);

    // Electric sparks along snake
    float sparks = sin(uv.x * 20.0 - Time * 10.0) * sin(uv.y * 15.0 + Time * 5.0);
    sparks = max(0.0, sparks) * 0.5 + 0.5;

    // Cyan-blue electric gradient
    vec3 electricColor = mix(vec3(0.2, 0.8, 1.0), vec3(0.0, 0.8, 1.0), sparks * 0.5);
    electricColor *= snake * noise;

    // Glow effect
    float glow = smoothstep(0.0, 0.3, dist) * snake;
    electricColor += vec3(0.2, 0.6, 1.0) * glow * 0.5;

    fragColor = vec4(electricColor * Progress, 0.8 * Progress);
}
