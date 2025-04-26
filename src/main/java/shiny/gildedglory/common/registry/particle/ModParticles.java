package shiny.gildedglory.common.registry.particle;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import shiny.gildedglory.GildedGlory;
import shiny.gildedglory.client.particle.effect.ColoredParticleEffect;

public class ModParticles {

    public static final DefaultParticleType SPARKLE = register("sparkle", FabricParticleTypes.simple());
    public static final DefaultParticleType SWIRL = register("swirl", FabricParticleTypes.simple());
    public static final DefaultParticleType ALERT = register("alert", FabricParticleTypes.simple());
    public static final DefaultParticleType SLASH = register("slash", FabricParticleTypes.simple());
    public static final DefaultParticleType ALTERNATE_SLASH = register("alternate_slash", FabricParticleTypes.simple());
    public static final DefaultParticleType VERTICAL_SLASH = register("vertical_slash", FabricParticleTypes.simple());
    public static final DefaultParticleType ALTERNATE_VERTICAL_SLASH = register("alternate_vertical_slash", FabricParticleTypes.simple());
    public static final ParticleType<ColoredParticleEffect> SQUARE = register("square", FabricParticleTypes.complex(false, ColoredParticleEffect.PARAMETERS_FACTORY));

    public static DefaultParticleType register(String name, DefaultParticleType particleType) {
        return Registry.register(Registries.PARTICLE_TYPE, GildedGlory.id(name), particleType);
    }

    private static <T extends ParticleEffect> ParticleType<T> register(String name, ParticleType<T> particleType) {
        return Registry.register(Registries.PARTICLE_TYPE, GildedGlory.id(name), particleType);
    }

    public static void registerModParticles() {
    }
}
