package shiny.gildedglory.client.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.MathHelper;

public class AlertParticle extends SpriteBillboardParticle {

    private final SpriteProvider spriteProvider;

    protected AlertParticle(ClientWorld world, double d, double e, double f, SpriteProvider spriteProvider) {
        super(world, d, e, f);

        this.gravityStrength = 0.0f;
        this.maxAge = 8;
        this.scale = 3f;
        this.spriteProvider = spriteProvider;

        this.setSpriteForAge(spriteProvider);
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteForAge(this.spriteProvider);
        this.scale = MathHelper.lerp((float) this.age / this.maxAge, 3f, 4.5f);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_LIT;
    }

    @Override
    public int getBrightness(float tint) {
        return 15728880;
    }

    public static class Factory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(SimpleParticleType type, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return new AlertParticle(clientWorld, d, e, f, this.spriteProvider);
        }
    }
}
