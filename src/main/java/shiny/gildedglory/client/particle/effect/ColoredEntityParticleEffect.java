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

public record ColoredEntityParticleEffect(ParticleType<ColoredEntityParticleEffect> type, int entityId, Vector3f offset, Vector3f color, float scale, int duration) implements ParticleEffect {

    public ColoredEntityParticleEffect(ParticleType<ColoredEntityParticleEffect> type, int entityId, Vector3f offset) {
        this(type, entityId, offset, new Vector3f(1.0f, 1.0f, 1.0f), 1.0f, 10);
    }

    public static final MapCodec<ColoredEntityParticleEffect> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Registries.PARTICLE_TYPE.getCodec().fieldOf("type").forGetter(ColoredEntityParticleEffect::type),
                    Codec.INT.fieldOf("id").forGetter(ColoredEntityParticleEffect::entityId),
                    Codecs.VECTOR_3F.fieldOf("offset").forGetter(ColoredEntityParticleEffect::offset),
                    Codecs.VECTOR_3F.fieldOf("color").forGetter(ColoredEntityParticleEffect::color),
                    Codec.FLOAT.fieldOf("scale").forGetter(ColoredEntityParticleEffect::scale),
                    Codec.INT.fieldOf("duration").forGetter(ColoredEntityParticleEffect::duration)
            ).apply(instance,
                    (type,
                     id,
                     offset,
                     vector,
                     scale,
                     duration) -> new ColoredEntityParticleEffect((ParticleType<ColoredEntityParticleEffect>) type, id, offset, vector, scale, duration)
            )
    );
    public static final PacketCodec<RegistryByteBuf, ColoredEntityParticleEffect> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.codec(Registries.PARTICLE_TYPE.getCodec()),
            ColoredEntityParticleEffect::type,
            PacketCodecs.INTEGER,
            ColoredEntityParticleEffect::entityId,
            PacketCodecs.VECTOR3F,
            ColoredEntityParticleEffect::offset,
            PacketCodecs.VECTOR3F,
            ColoredEntityParticleEffect::color,
            PacketCodecs.FLOAT,
            ColoredEntityParticleEffect::scale,
            PacketCodecs.INTEGER,
            ColoredEntityParticleEffect::duration,
            (type,
             id,
             offset,
             vector,
             scale,
             duration
            ) -> new ColoredEntityParticleEffect((ParticleType<ColoredEntityParticleEffect>) type, id, offset, vector, scale, duration)
    );

    @Override
    public ParticleType<?> getType() {
        return this.type;
    }
}
