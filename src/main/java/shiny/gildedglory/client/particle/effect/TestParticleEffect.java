package shiny.gildedglory.client.particle.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.util.dynamic.Codecs;
import org.joml.Vector3f;

public record TestParticleEffect(ParticleType<TestParticleEffect> type, Vector3f vector, float scale, int duration) implements ParticleEffect {

    public static final MapCodec<TestParticleEffect> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Registries.PARTICLE_TYPE.getCodec().fieldOf("type").forGetter(TestParticleEffect::type),
                    Codecs.VECTOR_3F.fieldOf("vector").forGetter(TestParticleEffect::vector),
                    Codec.FLOAT.fieldOf("scale").forGetter(TestParticleEffect::scale),
                    Codec.INT.fieldOf("duration").forGetter(TestParticleEffect::duration)
            ).apply(instance, (type, vector, scale, duration) -> new TestParticleEffect((ParticleType<TestParticleEffect>) type, vector, scale, duration))
    );
    public static final PacketCodec<RegistryByteBuf, TestParticleEffect> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.codec(Registries.PARTICLE_TYPE.getCodec()),
            TestParticleEffect::type,
            PacketCodecs.VECTOR3F,
            TestParticleEffect::vector,
            PacketCodecs.FLOAT,
            TestParticleEffect::scale,
            PacketCodecs.INTEGER,
            TestParticleEffect::duration,
            (type, vector, scale, duration) -> new TestParticleEffect((ParticleType<TestParticleEffect>) type, vector, scale, duration)
    );

    @Override
    public ParticleType<?> getType() {
        return this.type;
    }
}
