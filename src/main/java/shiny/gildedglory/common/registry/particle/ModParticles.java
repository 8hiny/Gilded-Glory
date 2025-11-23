package shiny.gildedglory.common.registry.particle;

import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import shiny.gildedglory.GildedGlory;
import shiny.gildedglory.client.particle.effect.*;

import java.util.function.Function;

public class ModParticles {

    public static final SimpleParticleType SPARKLE = register("sparkle");
    public static final SimpleParticleType SWIRL = register("swirl");
    public static final SimpleParticleType GOLD_SLASH = register("gold_slash");
    public static final SimpleParticleType TWISTEEL_SLASH = register("twisteel_slash");
    public static final SimpleParticleType IRAEDEUS_SLASH = register("iraedeus_slash");
    public static final SimpleParticleType GOLD_VERTICAL_SLASH = register("gold_vertical_slash");
    public static final SimpleParticleType TWISTEEL_VERTICAL_SLASH = register("twisteel_vertical_slash");
    public static final SimpleParticleType IRAEDEUS_VERTICAL_SLASH = register("iraedeus_vertical_slash");

    public static final ParticleType<VectorParticleEffect> SHOCKWAVE = register("shockwave",
            type -> VectorParticleEffect.CODEC,
            type -> VectorParticleEffect.PACKET_CODEC
    );
    public static final ParticleType<VectorParticleEffect> LARGE_IRAEDEUS_SLASH = register("large_iraedeus_slash",
            type -> VectorParticleEffect.CODEC,
            type -> VectorParticleEffect.PACKET_CODEC
    );
    public static final ParticleType<VectorParticleEffect> SQUARE = register("square",
            type -> VectorParticleEffect.CODEC,
            type -> VectorParticleEffect.PACKET_CODEC
    );
    public static final ParticleType<VectorParticleEffect> SHINE = register("shine",
            type -> VectorParticleEffect.CODEC,
            type -> VectorParticleEffect.PACKET_CODEC
    );
    public static final ParticleType<ColoredEntityParticleEffect> SHINE_ANIMATED = register("shine_animated",
            type -> ColoredEntityParticleEffect.CODEC,
            type -> ColoredEntityParticleEffect.PACKET_CODEC
    );
    public static final ParticleType<ColoredEntityParticleEffect> SWORDSPEAR_SHINE = register("swordspear_shine",
            type -> ColoredEntityParticleEffect.CODEC,
            type -> ColoredEntityParticleEffect.PACKET_CODEC
    );
    public static final ParticleType<ColoredEntityParticleEffect> ALERT = register("alert",
            type -> ColoredEntityParticleEffect.CODEC,
            type -> ColoredEntityParticleEffect.PACKET_CODEC
    );



    //public static final ParticleType<ColoredEntityParticleEffect> SHINE_ANIMATED = register("shine_animated", FabricParticleTypes.complex(true, ColoredEntityParticleEffect.PARAMETERS_FACTORY));

    private static SimpleParticleType register(String name) {
        return Registry.register(Registries.PARTICLE_TYPE, GildedGlory.id(name), FabricParticleTypes.simple());
    }

    private static <T extends ParticleEffect> ParticleType<T> register(
            String name, Function<ParticleType<T>, MapCodec<T>> codecGetter,
            Function<ParticleType<T>, PacketCodec<? super RegistryByteBuf, T>> packetCodecGetter
    ) {
        return Registry.register(Registries.PARTICLE_TYPE, GildedGlory.id(name), FabricParticleTypes.complex(codecGetter, packetCodecGetter));
    }

    public static void registerModParticles() {
    }
}
