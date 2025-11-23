package shiny.gildedglory.client.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import shiny.gildedglory.client.particle.custom.EntityAttachedParticle;
import shiny.gildedglory.client.particle.effect.ColoredEntityParticleEffect;

public class AlertParticle extends EntityAttachedParticle {

    private final SpriteProvider spriteProvider;

    protected AlertParticle(ClientWorld world, double x, double y, double z, ColoredEntityParticleEffect parameters, SpriteProvider spriteProvider) {
        super(world, x, y, z, parameters, spriteProvider);
        this.spriteProvider = spriteProvider;
        this.gravityStrength = 0.0f;
        this.maxAge = 5;
        this.scale = 1.5f;
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteForAge(this.spriteProvider);
        if (this.age > 1 && this.age < 4) this.scale = MathHelper.lerp((float) (this.age - 1) / 3, 1.5f, 2.5f);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_LIT;
    }

    @Override
    public int getBrightness(float tint) {
        return 15728880;
    }

    public static class Factory implements ParticleFactory<ColoredEntityParticleEffect> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(ColoredEntityParticleEffect parameters, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return new AlertParticle(clientWorld, d, e, f, parameters, this.spriteProvider);
        }
    }
}
