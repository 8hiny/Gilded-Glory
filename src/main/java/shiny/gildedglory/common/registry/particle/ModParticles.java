package shiny.gildedglory.common.registry.particle;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import shiny.gildedglory.GildedGlory;
import shiny.gildedglory.client.particle.effect.ColoredParticleEffect;

public class ModParticles {

    public static final DefaultParticleType SPARKLE = register("sparkle", FabricParticleTypes.simple());
    public static final DefaultParticleType SWIRL = register("swirl", FabricParticleTypes.simple());
    public static final DefaultParticleType ALERT = register("alert", FabricParticleTypes.simple());
    public static final DefaultParticleType GOLD_SLASH = register("gold_slash", FabricParticleTypes.simple());
    public static final DefaultParticleType TWISTEEL_SLASH = register("twisteel_slash", FabricParticleTypes.simple());
    public static final DefaultParticleType IRAEDEUS_SLASH = register("iraedeus_slash", FabricParticleTypes.simple());
    public static final DefaultParticleType GOLD_VERTICAL_SLASH = register("gold_vertical_slash", FabricParticleTypes.simple());
    public static final DefaultParticleType TWISTEEL_VERTICAL_SLASH = register("twisteel_vertical_slash", FabricParticleTypes.simple());
    public static final DefaultParticleType IRAEDEUS_VERTICAL_SLASH = register("iraedeus_vertical_slash", FabricParticleTypes.simple());
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
