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
//#define RAINBOW

float PI  = 3.14159265359;
float TAU = 6.28318530718;
float E = 2.71828182846;
float iters = 4.0;
float octaves = 8.0;

float wave(float x,float time) {
    return sin(x * PI - time) * 0.5 + 0.5;
}

vec2 rot(vec2 uv, float phi) {
    return vec2(sin(phi) * uv.x + cos(phi) * uv.y,cos(phi) * uv.x - sin(phi) * uv.y);
}

float rotfbm(vec3 pos) {
    float result = 0.;
    float m = 0.0;
    for(float i = 1.0; i < octaves; i++) {
        float tmp = 1.0 / i;
        m += tmp;
        result += wave(rot(pos.xy * i, PI * E * i).x * iters, pos.z) * tmp;
    }
    return result / m;
}

vec3 normalMap(vec2 uv, float time) {
    float p = rotfbm(vec3(uv, time));
    float h1 = rotfbm(vec3(uv + vec2(0.1, 0), time));
    float v1 = rotfbm(vec3(uv + vec2(0, 0.1), time));
    vec2 n2 = 0.5 + (p - vec2(h1, v1));
    return normalize(vec3(n2, 1.0));
}

float curvature(vec2 uv, float time) {
    vec3 p = normalMap(uv, time);
    vec3 h1 = normalMap(uv + vec2(0.001, 0), time);
    vec3 v1 = normalMap(uv + vec2(0, 0.001), time);

    return (h1.x -p.x + v1.y -p.y - p.z) * 0.5;
}

vec4 noiseSwirl(vec2 uv, float time) {
    float val = rotfbm(vec3(uv, time));
    vec2 pos = 0.25 * (uv + normalMap(0.5 * (uv + normalMap(1. * (uv + normalMap(uv, time).xy), time).yz), time).zx);

    vec3 col = normalMap(pos, time);
    float occ = curvature(pos, time);

    vec4 result = texture(NoiseSampler, uv + col.xy * col.z);
    result = result * result * 4.0;
    result = (result + occ * 0.1);
    return result;
}

vec4 rainbowSwirl(vec2 uv, float time) {
    vec3 v = normalize(vec3(uv, 1.0));
    vec3 p = vec3(0.0);
    vec3 c = p;

    float d = 0;
    float m = 100.0;
    float a = 9.0 + 0.1 * time;

    vec2 cs;
    vec2 o = vec2(0., .5 * PI);

    for(float i = 0.0; i < 5e1; i++) {
        d = length(p.xy - vec2(cos(o + p.z + time)));
        c += 0.01 * (sin(p) + 1.0) / max(d, 0.1);
        cs = cos(o - a);
        v.xy *= mat2(cs, -cs.y, cs.x);
        a *= 0.9;
        cs = cos(o - 0.1 * cos(a));
        v.xz *= mat2(cs, -cs.y, cs.x);
        p += d * v;
    }
    return vec4(c, 1.0);
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
        vec2 uv = (texCoord.xy * 2.0) - 1.0;

        #ifdef RAINBOW
        vec4 color = rainbowSwirl(uv, WorldTime * 0.01);
        #else
        vec4 color = noiseSwirl(uv, WorldTime * 0.0008);
        #endif

        float similarity = hueSimilarity(baseColor.rgb, YELLOW_REFERENCE) * 3.0;
        fragColor = baseColor * similarity + color * (1.0 - similarity);
    }
    else {
        fragColor = baseColor;
    }
}