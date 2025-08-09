package shiny.gildedglory.client.particle.effect;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.dynamic.Codecs;
import org.joml.Vector3f;

public class VectorParticleEffect implements ParticleEffect {

    private final ParticleType<VectorParticleEffect> type;
    private final Vector3f vector;


    public VectorParticleEffect(ParticleType<VectorParticleEffect> type, Vector3f vector) {
        this.type = type;
        this.vector = vector;
    }

    public static MapCodec<VectorParticleEffect> createCodec(ParticleType<VectorParticleEffect> type) {
        return Codecs.VECTOR_3F.xmap(vector -> new VectorParticleEffect(type, vector), effect -> effect.vector)
                .fieldOf("vector");
    }

    public static PacketCodec<? super RegistryByteBuf, VectorParticleEffect> createPacketCodec(ParticleType<VectorParticleEffect> type) {
        return PacketCodecs.VECTOR3F.xmap(vector -> new VectorParticleEffect(type, vector), effect -> effect.vector);
    }

    @Override
    public ParticleType<?> getType() {
        return this.type;
    }

    public Vector3f getVector() {
        return this.vector;
    }
}
