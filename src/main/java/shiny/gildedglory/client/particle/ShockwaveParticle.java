package shiny.gildedglory.client.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import shiny.gildedglory.client.particle.custom.DirectionalParticle;
import shiny.gildedglory.client.particle.effect.VectorParticleEffect;

public class ShockwaveParticle extends DirectionalParticle {

    private final float minScale;
    private final float maxScale;

    public ShockwaveParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, VectorParticleEffect parameters, SpriteProvider spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ, parameters, spriteProvider);
        this.scale = 3.0f;
        this.maxAge = 10;
        this.maxScale = this.scale;
        this.minScale = this.maxScale * 0.25f;
    }

    @Override
    public void tick() {
        super.tick();

        float step = (float) this.age / this.maxAge;
        this.alpha = 1.0f - step;
        this.scale = MathHelper.lerp(step, this.minScale, this.maxScale);
    }

    public static class Factory implements ParticleFactory<VectorParticleEffect> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(VectorParticleEffect parameters, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return new ShockwaveParticle(clientWorld, d, e, f, g, h, i, parameters, this.spriteProvider);
        }
    }
}
