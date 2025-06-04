#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D MirrorSampler;
uniform vec3 InSize;
uniform vec2 OutSize;

in vec2 texCoord;
in vec2 oneTexel;

out vec4 fragColor;

void main() {
    vec4 color = texture(DiffuseSampler, texCoord);
    fragColor = color;

    vec4 quadColor = texture(MirrorSampler, texCoord);
    if (quadColor == vec4(0.0, 0.0, 0.0, 1.0)) {
        //Apply refraction here
        vec2 uv = (texCoord.xy * 2.0) - 1.0;
        float distance = distance(uv.xy, vec2(0));

        fragColor = texture(DiffuseSampler, clamp(texCoord.xy * distance, 0.0, 0.5));
    }
}