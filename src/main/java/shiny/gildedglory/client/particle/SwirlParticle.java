package shiny.gildedglory.client.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
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

        this.scale = GildedGloryUtil.random(0.2f, 0.5f);
        this.maxAge = (int) GildedGloryUtil.random(20f, 30f);
        this.red = 1.0f;
        this.green = 1.0f;
        this.blue = 1.0f;

        this.collidesWithWorld = false;
        this.setSpriteForAge(spriteProvider);
    }

    @Override
    public int getBrightness(float tint) {
        return 15728880;
    }

    @Override
    public void tick() {
        if (!(this.age++ >= this.maxAge)) this.setSpriteForAge(this.spriteProvider);
        super.tick();
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_LIT;
    }

    public static class Factory implements ParticleFactory<DefaultParticleType> {

        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            SwirlParticle swirlParticle = new SwirlParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
            swirlParticle.setSprite(this.spriteProvider);
            return swirlParticle;
        }
    }
}
