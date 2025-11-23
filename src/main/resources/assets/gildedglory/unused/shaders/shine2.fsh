#version 150

uniform sampler2D DiffuseSampler;
uniform vec2 InSize;
uniform vec2 OutSize;

in vec2 texCoord;
in vec2 oneTexel;

out vec4 fragColor;

#define STREAK_SIZE 0.1
#define SAMPLES 0.005
#define THRESHOLD 0.2
#define INTENSITY 5.0

void main() {
    fragColor = texture(DiffuseSampler, texCoord);
    for (float i = -STREAK_SIZE; i < STREAK_SIZE; i += SAMPLES) {
        float falloff = 1.0 - abs(i / STREAK_SIZE);

        vec4 blur = texture(DiffuseSampler, texCoord + i);
        if (blur.r + blur.g + blur.b > THRESHOLD * 3.0) {
            fragColor += blur * falloff * SAMPLES * INTENSITY;
        }

        blur = texture(DiffuseSampler, texCoord + vec2(i, -i));
        if (blur.r + blur.g + blur.b > THRESHOLD * 3.0) {
            fragColor += blur * falloff * SAMPLES * INTENSITY;
        }
    }
}
