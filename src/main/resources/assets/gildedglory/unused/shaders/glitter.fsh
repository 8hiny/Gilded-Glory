#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D EntitySampler;
uniform sampler2D NoiseSampler;

uniform vec2 InSize;
uniform vec2 OutSize;

uniform float WorldTime;

in vec2 texCoord;
in vec2 oneTexel;

out vec4 fragColor;

#define YELLOW_REFERENCE vec3(1.0, 1.0, 0.2)

float time = WorldTime * 0.01;
float PI  = 3.14159265359;
float TAU = 6.28318530718;

vec4 permute(vec4 x) {
    return mod(((x * 34.0) + 1.0) * x, 289.0);
}

vec4 taylorInvSqrt(vec4 r) {
    return 1.79284291400159 - 0.85373472095314 * r;
}

float snoise(vec3 v){
    const vec2 C = vec2(1.0 / 6.0, 1.0 / 3.0);
    const vec4 D = vec4(0.0, 0.5, 1.0, 2.0);

    vec3 i = floor(v + dot(v, C.yyy));
    vec3 x0 = v - i + dot(i, C.xxx);

    vec3 g = step(x0.yzx, x0.xyz);
    vec3 l = 1.0 - g;
    vec3 i1 = min(g.xyz, l.zxy);
    vec3 i2 = max(g.xyz, l.zxy);

    vec3 x1 = x0 - i1 + 1.0 * C.xxx;
    vec3 x2 = x0 - i2 + 2.0 * C.xxx;
    vec3 x3 = x0 - 1. + 3.0 * C.xxx;

    i = mod(i, 289.0);
    vec4 p = permute(permute(permute(
    i.z + vec4(0.0, i1.z, i2.z, 1.0)) +
    i.y + vec4(0.0, i1.y, i2.y, 1.0)) +
    i.x + vec4(0.0, i1.x, i2.x, 1.0)
    );

    float n_ = 1.0 / 7.0;
    vec3  ns = n_ * D.wyz - D.xzx;

    vec4 j = p - 49.0 * floor(p * ns.z *ns.z);

    vec4 x_ = floor(j * ns.z);
    vec4 y_ = floor(j - 7.0 * x_);

    vec4 x = x_ * ns.x + ns.yyyy;
    vec4 y = y_ * ns.x + ns.yyyy;
    vec4 h = 1.0 - abs(x) - abs(y);

    vec4 b0 = vec4(x.xy, y.xy);
    vec4 b1 = vec4(x.zw, y.zw);

    vec4 s0 = floor(b0) * 2.0 + 1.0;
    vec4 s1 = floor(b1) * 2.0 + 1.0;
    vec4 sh = -step(h, vec4(0.0));

    vec4 a0 = b0.xzyw + s0.xzyw * sh.xxyy ;
    vec4 a1 = b1.xzyw + s1.xzyw * sh.zzww ;

    vec3 p0 = vec3(a0.xy, h.x);
    vec3 p1 = vec3(a0.zw, h.y);
    vec3 p2 = vec3(a1.xy, h.z);
    vec3 p3 = vec3(a1.zw, h.w);

    vec4 norm = taylorInvSqrt(vec4(dot(p0, p0), dot(p1, p1), dot(p2, p2), dot(p3, p3)));
    p0 *= norm.x;
    p1 *= norm.y;
    p2 *= norm.z;
    p3 *= norm.w;

    vec4 m = max(0.6 - vec4(dot(x0, x0), dot(x1, x1), dot(x2, x2), dot(x3, x3)), 0.0);
    m = m * m;
    return 42.0 * dot(m*m, vec4(dot(p0, x0), dot(p1, x1), dot(p2, x2), dot(p3, x3)));
}

vec3 contrastSaturationBrightness(vec3 color, float brt, float sat, float con){
    const float AvgLumR = 0.5;
    const float AvgLumG = 0.5;
    const float AvgLumB = 0.5;

    const vec3 LumCoeff = vec3(0.2125, 0.7154, 0.0721);

    vec3 AvgLumin  = vec3(AvgLumR, AvgLumG, AvgLumB);
    vec3 brtColor  = color * brt;
    vec3 intensity = vec3(dot(brtColor, LumCoeff));
    vec3 satColor  = mix(intensity, brtColor, sat);
    vec3 conColor  = mix(AvgLumin, satColor, con);

    return conColor;
}

vec3 exposure(vec3 color, float exposure) {
    return color * pow(2., exposure);
}

float randomNoise(vec2 uv, float time) {
    float aspect = OutSize.x / OutSize.y;
    float result = 0.;

    float scale = 0.0017;
    vec2 absoluteSize = OutSize.xy / vec2(aspect, 1.);

    for(float i = 1.; i <= 2.0; i++){
        vec2 backUV = uv * (i) * absoluteSize * scale;
        backUV.y += time;

        vec2 frontUV = uv * (i + 3.14) * absoluteSize * scale;
        frontUV.y -= time;

        result +=texture(NoiseSampler, backUV).r * texture(NoiseSampler, frontUV).g;
    }
    return pow(result + 0.2, 12.);
}

vec3 colorFromBrightness(float brightness) {
    return vec3(pow(brightness, 0.4), pow(brightness, 0.48), pow(brightness, 0.62));
}

vec3 formula(vec2 uv) {
    float displacementScale = 1.5;
    float displacementSpeed = 0.08;
    float displacementStrength = 0.075;

    float displacement = snoise(vec3(uv * displacementScale, time * displacementSpeed));
    displacement = displacement * 0.5 + 0.5;

    uv.x += displacementStrength * sin(displacement * TAU);
    uv.y += displacementStrength * cos(displacement * TAU);

    float noiseScale = 3.0;
    float speed = 0.05;
    float noiseTime = time * speed;
    float noise = snoise(vec3(uv * noiseScale, noiseTime));
    noise = pow(noise * 0.5 + 0.55, 8.);

    return vec3(noise);
}

vec3 gs = vec3(0.21, 0.72, 0.07);

vec3 bump(vec2 p, float e) {
    vec2 h = vec2(e, 0.0);
    mat3 m = mat3(
    formula(p + h) - formula(p - h), formula(p + h.yx) - formula(p - h.yx), -0.3 * gs);

    vec3 g = (gs * m) / e;

    return normalize(g);
}

float edge(vec2 p, float e) {
    vec2 h = vec2(e, 0.0);
    float d = dot(gs, formula(p));
    vec3 n1 = gs * mat3(formula(p + h.xy), formula(p + h.yx), vec3(0));
    vec3 n2 = gs * mat3(formula(p - h.xy), formula(p - h.yx), vec3(0));

    vec3 vv = abs(d - 0.5 * (n1 + n2));
    float v = min(1.0, pow(vv.x + vv.y + vv.z, 0.55) * 1.0);

    return v;
}

vec3 rgbToHcv(vec3 rgb) {
    vec4 p = (rgb.g < rgb.b)
    ? vec4(rgb.bg, -1.0, 2.0 / 3.0)
    : vec4(rgb.gb, 0.0, -1.0 / 3.0);
    vec4 q = (rgb.r < p.x)
    ? vec4(p.xyw, rgb.r)
    : vec4(rgb.r, p.yzx);

    float chroma = q.x - min(q.w, q.y);
    float hue = abs((q.w - q.y) / (6.0 * chroma + 1e-5) + q.z);
    return vec3(hue, chroma, q.x);
}

float hueSimilarity(vec3 colorA, vec3 colorB) {
    vec3 a = rgbToHcv(colorA);
    vec3 b = rgbToHcv(colorB);

    float hueDiff = min(abs(a.x - b.x), 1.0 - abs(a.x - b.x));
    float satDiff = abs(a.y - b.y);
    float valDiff = abs(a.z - b.z);

    return (hueDiff + valDiff * 0.35 + satDiff * 0.35) / (1.0 + 0.55 + 0.35);
}

void main() {
    vec4 baseColor = texture(DiffuseSampler, texCoord);
    vec4 entityColor = texture(EntitySampler, texCoord);

    if (entityColor != vec4(0.0)) {
        float aspect = OutSize.x / OutSize.y;
        vec2 uv = (texCoord.xy * 2.0) - 1.0;
        uv -= 0.5;
        uv.x *= aspect;
        uv.y -= time * 0.01;


        vec3 rd = normalize(vec3(uv, 1.0));

        vec3 sn = bump(uv, 0.01);
        vec3 re = reflect(rd, sn);
        float col = 0.;

        col += 0.5 * clamp(dot(-rd, sn), 0.0, 1.0);
        col += 0.8 * pow(clamp(1.0 + dot(rd, sn), 0.0, 1.0), 8.0);
        float f = formula(uv).x;
        col *= f;
        col += pow(clamp(dot(-rd, re), 0.0, 1.0), 8.0) * (8.0 * f);

        col *= edge(uv, 0.01);

        col = pow(col, 1.0 / 2.2);

        uv.x += 0.0025 * sin(f * TAU);
        uv.y += 0.0025 * cos(f * TAU);


        float result = randomNoise(uv, time * 0.01);

        vec2 mp = vec2(.5, .5);
        mp -= 0.5;
        mp.x *= aspect;
        mp.y -= time * 0.01;
        col *= (pow(max(0.0, 1.0 - length(mp - uv) * 0.8), 6.0) + 0.01);

        vec3 fincol = colorFromBrightness(result * col + 0.03 * col);

        fincol = exposure(fincol, 2.);
        fincol = contrastSaturationBrightness(fincol, 1.8, 1.4, 1.0);

        vec4 color = vec4(fincol, 1.0);
        float similarity = hueSimilarity(baseColor.rgb, YELLOW_REFERENCE) * 3.0;
        fragColor = baseColor * similarity + color * (1.0 - similarity);
    }
    else {
        fragColor = vec4(0.0);
    }
}