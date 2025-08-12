package shiny.gildedglory.client.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

public class AlertParticle extends SpriteBillboardParticle {

    private final SpriteProvider spriteProvider;

    protected AlertParticle(ClientWorld world, double d, double e, double f, double g, SpriteProvider spriteProvider) {
        super(world, d, e, f);

        this.gravityStrength = 0.0f;
        this.maxAge = 8;
        this.scale = 1.75f;
        this.spriteProvider = spriteProvider;

        this.setSpriteForAge(spriteProvider);
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteForAge(this.spriteProvider);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_LIT;
    }

    @Override
    public int getBrightness(float tint) {
        float f = ((float) this.age + tint) / (float)this.maxAge;
        f = MathHelper.clamp(f, 0.0F, 1.0F);
        int i = super.getBrightness(tint);
        int j = i & 0xFF;
        int k = i >> 16 & 0xFF;
        j += (int) (f * 15.0F * 16.0F);
        if (j > 240) {
            j = 240;
        }

        return j | k << 16;
    }

    public static class Factory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(SimpleParticleType type, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return new AlertParticle(clientWorld, d, e, f, g, this.spriteProvider);
        }
    }
}
