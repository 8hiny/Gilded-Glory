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

public record VectorParticleEffect(ParticleType<VectorParticleEffect> type, Vector3f vector, float scale, int duration) implements ParticleEffect {

    public static final MapCodec<VectorParticleEffect> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Registries.PARTICLE_TYPE.getCodec().fieldOf("type").forGetter(VectorParticleEffect::type),
                    Codecs.VECTOR_3F.fieldOf("vector").forGetter(VectorParticleEffect::vector),
                    Codec.FLOAT.fieldOf("scale").forGetter(VectorParticleEffect::scale),
                    Codec.INT.fieldOf("duration").forGetter(VectorParticleEffect::duration)
            ).apply(instance, (type, vector, scale, duration) -> new VectorParticleEffect((ParticleType<VectorParticleEffect>) type, vector, scale, duration))
    );
    public static final PacketCodec<RegistryByteBuf, VectorParticleEffect> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.codec(Registries.PARTICLE_TYPE.getCodec()),
            VectorParticleEffect::type,
            PacketCodecs.VECTOR3F,
            VectorParticleEffect::vector,
            PacketCodecs.FLOAT,
            VectorParticleEffect::scale,
            PacketCodecs.INTEGER,
            VectorParticleEffect::duration,
            (type, vector, scale, duration) -> new VectorParticleEffect((ParticleType<VectorParticleEffect>) type, vector, scale, duration)
    );

    @Override
    public ParticleType<?> getType() {
        return this.type;
    }
}
