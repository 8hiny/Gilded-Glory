package shiny.gildedglory.client.particle.custom;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import org.joml.Vector3f;
import shiny.gildedglory.client.particle.effect.VectorParticleEffect;

public class SimpleColoredParticle extends SpriteBillboardParticle {

    private final SpriteProvider spriteProvider;

    public SimpleColoredParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, VectorParticleEffect parameters, SpriteProvider spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.spriteProvider = spriteProvider;

        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.velocityZ = velocityZ;

        Vector3f color = parameters.vector();
        this.red = color.x;
        this.green = color.y;
        this.blue = color.z;
        this.scale = parameters.scale();

        this.maxAge = (int) (parameters.duration() + this.random.nextInt(10) * Math.max(parameters.scale(), 1.0f));

        this.setSpriteForAge(spriteProvider);
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteForAge(this.spriteProvider);
    }

    @Override
    public int getBrightness(float tint) {
        return 15728880;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Factory implements ParticleFactory<VectorParticleEffect> {

        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(VectorParticleEffect parameters, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return new SimpleColoredParticle(clientWorld, d, e, f, g, h, i, parameters, this.spriteProvider);
        }
    }
}