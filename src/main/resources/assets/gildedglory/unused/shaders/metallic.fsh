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

float time = WorldTime * 2;

vec2 hash2(float n){
    return fract(sin(vec2(n,n+1.0))*vec2(13.5453123,31.1459123));
}

float noise(in vec2 x) {
    vec2 p = floor(x);
    vec2 f = fract(x);
    f = f * f * (3.0 - 2.0 * f);
    float a = textureLod(NoiseSampler,(p + vec2(0.5, 0.5)) / 256.0, 0.0).x;
    float b = textureLod(NoiseSampler,(p + vec2(1.5, 0.5)) / 256.0, 0.0).x;
    float c = textureLod(NoiseSampler,(p + vec2(0.5, 1.5)) / 256.0, 0.0).x;
    float d = textureLod(NoiseSampler,(p + vec2(1.5, 1.5)) / 256.0, 0.0).x;
    return mix(mix( a, b, f.x), mix( c, d, f.x), f.y);
}

const mat2 mtx = mat2(0.80,  0.60, -0.60,  0.80);

float fbm(vec2 p) {
    float f = 0.0;
    f += 0.500000*noise(p); p = mtx*p*2.02;
    f += 0.250000*noise(p); p = mtx*p*2.03;
    f += 0.125000*noise(p); p = mtx*p*2.01;
    f += 0.062500*noise(p); p = mtx*p*2.04;
    f += 0.031250*noise(p); p = mtx*p*2.01;
    f += 0.015625*noise(p);

    return f/0.96875;
}

float pattern(in vec2 p, in float t, in vec2 uv, out vec2 q, out vec2 r, out vec2 g) {
    q = vec2(fbm(p), fbm(p + vec2(10, 1.3)));

    float s = dot(uv.x + 0.5, uv.y + 0.5);
    r = vec2(fbm(p + 4.0 * q + vec2(t) + vec2(1.7, 9.2)), fbm(p + 4.0 * q + vec2(t) + vec2(8.3, 2.8)));
    g = vec2(fbm(p + 2.0 * r + vec2(t * 20.0) + vec2(2, 6)), fbm(p + 2.0 * r + vec2(t * 10.0) + vec2(5, 3)));
    return fbm(p + 5.5 * g + vec2(-t * 7.0));
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
        // noise
        vec2 q, r, g;
        float noise = pattern(texCoord * OutSize * vec2(.004), time * 0.007, texCoord, q, r, g);

        // base color based on main noise
        vec3 col = mix(vec3(0.1, 0.4, 0.4), vec3(0.5, 0.7, 0.0), smoothstep(0.0, 1.0, noise));

        // other lower-octave colors and mixes
        col = mix(col, vec3(0.35, 0.0, 0.1), dot(q, q) * 1.0);
        col = mix(col, vec3(0, 0.2, 1), 0.2 * g.y * g.y);
        col = mix(col, vec3(.3, 0, 0), smoothstep(0.0, .6, 0.6 * r.g * r.g));
        col = mix(col, vec3(0, .5, 0), 0.1 * g.x);

        // some dark outlines/contrast and different steps
        col = mix(col, vec3(0), smoothstep(0.3, 0.5, noise) * smoothstep(0.5, 0.3, noise));
        col = mix(col, vec3(0), smoothstep(0.7, 0.8, noise) * smoothstep(0.8, 0.7, noise));

        // contrast
        col *= noise * 2.0;

        vec4 color = vec4(col,1.0);
        float similarity = hueSimilarity(baseColor.rgb, YELLOW_REFERENCE);
        fragColor = baseColor * similarity + color * (1.0 - similarity * 0.5);
    }
    else {
        fragColor = vec4(0.0);
    }
}