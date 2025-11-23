package shiny.gildedglory.client.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import shiny.gildedglory.common.util.GildedGloryUtil;

public class SwirlParticle extends SpriteBillboardParticle {

    private final SpriteProvider spriteProvider;

    SwirlParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, SpriteProvider spriteProvider) {
        super(clientWorld, d, e, f, 0.0, 0.0, 0.0);

        this.spriteProvider = spriteProvider;

        this.velocityMultiplier = 0.7f;
        this.gravityStrength = 0.5f;
        this.velocityX *= 0.1f;
        this.velocityY *= 0.1f;
        this.velocityZ *= 0.1f;
        this.velocityX += g * 0.4;
        this.velocityY += h * 0.4;
        this.velocityZ += i * 0.4;

        this.scale = GildedGloryUtil.random(0.3f, 0.6f);
        this.maxAge = 10 + this.random.nextInt(12);
        this.setSpriteForAge(spriteProvider);
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteForAge(this.spriteProvider);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    public static class Factory implements ParticleFactory<SimpleParticleType> {

        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(SimpleParticleType type, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return new SwirlParticle(clientWorld, d, e, f, g, h, i, spriteProvider);
        }
    }
}
