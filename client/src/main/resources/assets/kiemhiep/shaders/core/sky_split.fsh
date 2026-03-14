#version 150

uniform sampler2D DiffuseSampler;
uniform vec2 SplitCenter;
uniform float SplitWidth;
uniform float SplitLength;
uniform float Progress;
uniform float Time;
uniform vec2 ScreenSize;

out vec4 fragColor;

// Noise function cho edge irregularities
float hash(vec2 p) {
    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453);
}

float noise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    float a = hash(i);
    float b = hash(i + vec2(1.0, 0.0));
    float c = hash(i + vec2(0.0, 1.0));
    float d = hash(i + vec2(1.0, 1.0));
    vec2 u = f * f * (3.0 - 2.0 * f);
    return mix(a, b, u.x) + (c - a) * u.y * (1.0 - u.x) + (d - b) * u.x * u.y;
}

void main() {
    vec2 uv = gl_FragCoord.xy / ScreenSize;
    vec2 diff = uv - SplitCenter;
    float angle = atan(diff.y, diff.x);
    float dist = length(diff);

    // Tạo vết nứt hình chữ V với noise cho edge tự nhiên
    float baseAngle = SplitLength * 0.5;
    float noiseEdge = noise(vec2(angle * 10.0, Time * 2.0)) * 0.1;

    float crackEdge1 = abs(angle - baseAngle);
    float crackEdge2 = abs(angle + baseAngle);

    float crackMask = smoothstep(0.0, SplitWidth + noiseEdge, crackEdge1);
    crackMask *= smoothstep(0.0, SplitWidth + noiseEdge, crackEdge2);
    crackMask = 1.0 - crackMask;

    // Displacement UV - đẩy 2 bên ra xa vết nứt
    float displacementAmount = crackMask * Progress * 0.08;
    vec2 displacement = normalize(diff) * displacementAmount;
    vec2 splitUV = uv + displacement;

    // Sample color từ texture
    vec4 skyColor = texture(DiffuseSampler, splitUV);

    // Làm tối 2 bên vết nứt (vignette effect)
    float darkenFactor = 0.3 + 0.5 * (1.0 - crackMask);
    vec3 darkenedSky = skyColor.rgb * mix(1.0, darkenFactor, Progress);

    // Tạo màu đỏ/cam cho vết nứt (lava glow với pulsing effect)
    float pulse = sin(Time * 5.0) * 0.2 + 0.8;
    vec3 crackGlow = vec3(1.0, 0.4, 0.1) * crackMask * pulse;

    // Thêm core màu trắng/xanh ở giữa vết nứt (năng lượng)
    float crackCore = smoothstep(SplitWidth * 0.3, 0.0, crackEdge1) *
                      smoothstep(SplitWidth * 0.3, 0.0, crackEdge2);
    vec3 energyCore = vec3(0.8, 0.9, 1.0) * crackCore * crackMask * Progress;

    // Composite final color
    vec3 finalColor = mix(darkenedSky, crackGlow, crackMask * Progress * 0.7);
    finalColor = mix(finalColor, energyCore, crackCore * Progress * 0.5);

    fragColor = vec4(finalColor, skyColor.a);
}
