package shiny.gildedglory.client.particle.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.dynamic.Codecs;
import org.joml.Vector3f;
import shiny.gildedglory.common.registry.particle.ModParticles;

public record ChargeParticleEffect(Vector3f vector, float scale, int duration) implements ParticleEffect {

    public static final MapCodec<ChargeParticleEffect> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Codecs.VECTOR_3F.fieldOf("vector").forGetter(ChargeParticleEffect::vector),
                    Codec.FLOAT.fieldOf("scale").forGetter(ChargeParticleEffect::scale),
                    Codec.INT.fieldOf("duration").forGetter(ChargeParticleEffect::duration)
            ).apply(instance, ChargeParticleEffect::new)
    );
    public static final PacketCodec<RegistryByteBuf, ChargeParticleEffect> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.VECTOR3F,
            ChargeParticleEffect::vector,
            PacketCodecs.FLOAT,
            ChargeParticleEffect::scale,
            PacketCodecs.INTEGER,
            ChargeParticleEffect::duration,
            ChargeParticleEffect::new
    );

    @Override
    public ParticleType<?> getType() {
        return ModParticles.SQUARE;
    }
}
