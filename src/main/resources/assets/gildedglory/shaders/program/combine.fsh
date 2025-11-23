#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D EntitySampler;
uniform sampler2D SparkleSampler;

uniform vec2 InSize;
uniform vec2 OutSize;

in vec2 texCoord;
in vec2 oneTexel;

out vec4 fragColor;

//#define APPLY_SECONDARY_SHINE

void main() {
    vec4 baseColor = texture(DiffuseSampler, texCoord);
    vec4 entityColor = texture(EntitySampler, texCoord);
    vec4 sparkleColor = texture(SparkleSampler, texCoord);

    if (entityColor != vec4(0.0)) {
        #ifdef APPLY_SECONDARY_SHINE
        fragColor = entityColor * 0.5 + sparkleColor * 0.5;
        #else
        fragColor = entityColor;
        #endif
    }
    #ifdef APPLY_SECONDARY_SHINE
    else if (sparkleColor != vec4(0.0)) {
        fragColor = sparkleColor;
    }
    #endif
    else {
        fragColor = baseColor;
    }
}