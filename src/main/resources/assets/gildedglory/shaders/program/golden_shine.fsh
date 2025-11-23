#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D EntitySampler;
uniform vec2 InSize;

in vec2 texCoord;
in vec2 oneTexel;

out vec4 fragColor;

//TODO Fix depth not applying

//--- Original by n1tr0n__ (@Nitron) ---\\

#define BLOOM_INTENSITY 1.35  //The brightness of the spiked bloom
#define BLOOM_SPREAD 25.0  //The amount by which the bloom protrudes
#define BLOOM_SAMPLES 15.0  //How many samples to take for the bloom; Has a high performance impact
#define YELLOW_REFERENCE vec3(1.0, 1.0, 0.2)  //The shade of color to compare to for how much bloom should be applied
//#define CHECK_ENTITY  //Whether to check if the current pixel should be affected or not

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

    //Perceived saturation boost — even in low light
    float saturation = length(color - vec3(dot(color, vec3(0.333))));
    float recovery = smoothstep(0.0, 0.3, saturation);

    return smoothstep(0.77, 0.90, hueAffinity) * (0.5 + recovery * 0.5);
}

vec3 recoverColor(vec3 color) {
    //Estimate luminance
    float luminance = dot(color, vec3(0.299, 0.587, 0.114));

    //If luminance is very low but hue is distinct, boost it
    float boost = smoothstep(0.05, 0.2, length(color)) * (1.0 - smoothstep(0.0, 0.15, luminance));
    return mix(color, normalize(color + 0.001) * 0.8, boost);
}

vec4 gatherSamples(vec2 uv) {
    vec2 offset = BLOOM_SPREAD * oneTexel;
    vec4 base = texture(DiffuseSampler, uv);
    base.rgb = recoverColor(base.rgb);
    vec4 result = base * yellowAffinity(base.rgb);

    for (float i = 1.0; i <= BLOOM_SAMPLES; i++) {
        vec2 delta = offset / i;

        vec4 s1 = texture(EntitySampler, uv + delta);
        vec4 s2 = texture(EntitySampler, uv - delta);
        vec4 s3 = texture(EntitySampler, uv + vec2(delta.x, -delta.y));
        vec4 s4 = texture(EntitySampler, uv - vec2(delta.x, -delta.y));

        s1.rgb = recoverColor(s1.rgb);
        s2.rgb = recoverColor(s2.rgb);
        s3.rgb = recoverColor(s3.rgb);
        s4.rgb = recoverColor(s4.rgb);

        result += s1 * yellowAffinity(s1.rgb);
        result += s2 * yellowAffinity(s2.rgb);
        result += s3 * yellowAffinity(s3.rgb);
        result += s4 * yellowAffinity(s4.rgb);
    }
    return (result) / (3.0 * BLOOM_SAMPLES);
}

bool isNearShine(vec2 uv, sampler2D tex) {
    vec2 offset = BLOOM_SPREAD / 2 * oneTexel;
    for (float i = 1.0; i <= BLOOM_SAMPLES; i ++) {
        vec2 delta = offset / i;

        vec4 s1 = texture(tex, uv + delta);
        vec4 s2 = texture(tex, uv - delta);
        vec4 s3 = texture(tex, uv + vec2(delta.x, -delta.y));
        vec4 s4 = texture(tex, uv - vec2(delta.x, -delta.y));

        if (s1 != vec4(0.0) || s2 != vec4(0.0) || s3 != vec4(0.0) || s4 != vec4(0.0)) {
            return true;
        }
    }
    return false;
}

void main() {
    vec4 baseColor = texture(DiffuseSampler, texCoord);

    #ifdef CHECK_ENTITY
    vec4 entityColor = texture(EntitySampler, texCoord);
    if (entityColor != vec4(0.0) || isNearShine(texCoord, EntitySampler)) {
    #endif

        vec4 bloom = gatherSamples(texCoord);

        //Adaptive boost based on darkness — stronger bloom when darker
        float sceneBrightness = dot(baseColor.rgb, vec3(0.299, 0.587, 0.114));
        float adaptiveBoost = mix(4.0, 1.0, smoothstep(0.1, 0.4, sceneBrightness));

        vec4 bright = clamp(bloom * BLOOM_INTENSITY * adaptiveBoost, 0.0, 1.0);

        fragColor = 1.0 - (1.0 - baseColor) * (1.0 - bright);
        fragColor.a = 1.0;

    #ifdef CHECK_ENTITY
    }
    else {
        fragColor = baseColor;
    }
    #endif
}
