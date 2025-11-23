#version 150

uniform sampler2D DiffuseSampler;
uniform vec2 InSize;

in vec2 texCoord;
in vec2 oneTexel;

out vec4 fragColor;

#define BLOOM_INTENSITY 1.5
#define BLOOM_SPREAD 25.0
#define BLOOM_SAMPLES 15.0
#define YELLOW_REFERENCE vec3(1.0, 1.0, 0.2)
#define EVIL_DARK_REFERENCE vec3(2.0, 2.3, 2.5)

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

float yellowAffinity(vec3 color) {
    float diff = hueSimilarity(color, YELLOW_REFERENCE);
    float hueAffinity = 1.0 - diff;

    // Perceived saturation boost — even in low light
    float saturation = length(color - vec3(dot(color, vec3(0.333))));
    float recovery = smoothstep(0.0, 0.3, saturation);

    return smoothstep(0.77, 0.90, hueAffinity) * (0.5 + recovery * 0.5);
}

float darkAffinity(vec3 color) {
    float diff = hueSimilarity(color, EVIL_DARK_REFERENCE);
    float hueAffinity = 1.0 - diff;

    // Perceived saturation boost — even in low light
    float saturation = length(color - vec3(dot(color, vec3(0.333))));
    float recovery = smoothstep(0.0, 0.3, saturation);

    return smoothstep(0.77, 0.90, hueAffinity) * (0.5 + recovery * 0.5);
}

vec3 recoverColor(vec3 color) {
    // Estimate luminance
    float luminance = dot(color, vec3(0.299, 0.587, 0.114));

    // If luminance is very low but hue is distinct, boost it
    float boost = smoothstep(0.05, 0.2, length(color)) * (1.0 - smoothstep(0.0, 0.15, luminance));
    return mix(color, normalize(color + 0.001) * 0.8, boost);
}

vec4 gatherSamples(vec2 uv, sampler2D tex, float spread) {
    vec2 offset = spread * oneTexel;
    vec4 base = texture(tex, uv);
    base.rgb = recoverColor(base.rgb);
    vec4 result = base * yellowAffinity(base.rgb);
    vec4 dresult = base * darkAffinity(base.rgb);

    for (float i = 1.0; i <= BLOOM_SAMPLES; i += 1.0) {
        vec2 delta = offset / i;

        vec4 s1 = texture(tex, uv + delta);
        vec4 s2 = texture(tex, uv - delta);
        vec4 s3 = texture(tex, uv + vec2(delta.x, -delta.y));
        vec4 s4 = texture(tex, uv - vec2(delta.x, -delta.y));

        s1.rgb = recoverColor(s1.rgb);
        s2.rgb = recoverColor(s2.rgb);
        s3.rgb = recoverColor(s3.rgb);
        s4.rgb = recoverColor(s4.rgb);

        result += s1 * yellowAffinity(s1.rgb);
        result += s2 * yellowAffinity(s2.rgb);
        result += s3 * yellowAffinity(s3.rgb);
        result += s4 * yellowAffinity(s4.rgb);

        dresult += s1 * darkAffinity(s1.rgb);
        dresult += s2 * darkAffinity(s2.rgb);
        dresult += s3 * darkAffinity(s3.rgb);
        dresult += s4 * darkAffinity(s4.rgb);
    }

    return (result) / (3.0 * BLOOM_SAMPLES);
}

vec4 gatherSamplesRed(vec2 uv, sampler2D tex, float spread) {
    vec2 offset = spread * oneTexel;
    vec4 base = texture(tex, uv);
    base.rgb = recoverColor(base.rgb);
    vec4 result = base * yellowAffinity(base.rgb);
    vec4 dresult = base * darkAffinity(base.rgb);

    for (float i = 1.0; i <= BLOOM_SAMPLES; i += 1.0) {
        vec2 delta = offset / i;

        vec4 s1 = texture(tex, uv + delta);
        vec4 s2 = texture(tex, uv - delta);
        vec4 s3 = texture(tex, uv + vec2(delta.x, -delta.y));
        vec4 s4 = texture(tex, uv - vec2(delta.x, -delta.y));

        s1.rgb = recoverColor(s1.rgb);
        s2.rgb = recoverColor(s2.rgb);
        s3.rgb = recoverColor(s3.rgb);
        s4.rgb = recoverColor(s4.rgb);

        result += s1 * yellowAffinity(s1.rgb);
        result += s2 * yellowAffinity(s2.rgb);
        result += s3 * yellowAffinity(s3.rgb);
        result += s4 * yellowAffinity(s4.rgb);

        dresult += s1 * darkAffinity(s1.rgb);
        dresult += s2 * darkAffinity(s2.rgb);
        dresult += s3 * darkAffinity(s3.rgb);
        dresult += s4 * darkAffinity(s4.rgb);
    }

    return (dresult) / (8.0 * BLOOM_SAMPLES);
}

void main() {
    vec4 baseColor = texture(DiffuseSampler, texCoord);
    vec4 bloom = gatherSamples(texCoord, DiffuseSampler, BLOOM_SPREAD);
    vec4 bloomR = gatherSamplesRed(texCoord, DiffuseSampler, BLOOM_SPREAD);

    // Adaptive boost based on darkness — stronger bloom when darker
    float sceneBrightness = dot(baseColor.rgb, vec3(0.299, 0.587, 0.114));
    float adaptiveBoost = mix(4.0, 1.0, smoothstep(0.1, 0.4, sceneBrightness));

    vec4 bright = clamp(bloom * BLOOM_INTENSITY * adaptiveBoost, 0.0, 1.0);
    vec4 brightR = clamp(bloomR * BLOOM_INTENSITY * adaptiveBoost, 0.0, 1.0);

    fragColor = 1.0 - (1.0 - baseColor) * (1.0 - bright);
    fragColor.a = 1.0;
}
