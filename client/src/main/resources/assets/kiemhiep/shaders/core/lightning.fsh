#version 150

uniform sampler2D DiffuseSampler;
uniform float Progress;
uniform float Time;
uniform vec2 ScreenSize;

out vec4 fragColor;

void main() {
    vec2 uv = gl_FragCoord.xy / ScreenSize;

    // Pattern sấm sét - chớp trắng
    float noise = sin(uv.x * 15.0 + Time * 10.0) * sin(uv.y * 15.0 + Time * 8.0) * 0.5 + 0.5;

    // Gradient chớp: trắng -> xanh lam -> tím
    vec3 lightningColor = mix(vec3(1.0, 1.0, 1.0), vec3(0.3, 0.5, 1.0), noise);
    lightningColor = mix(lightningColor, vec3(0.5, 0.3, 1.0), Progress);

    // Hiệu ứng strobe chớp
    float strobe = sin(Time * 50.0) * 0.5 + 0.5;
    strobe = step(0.8, strobe); // Tạo nhịp chớp

    fragColor = vec4(lightningColor * (1.0 + strobe * 0.5), 0.7 * Progress);
}
