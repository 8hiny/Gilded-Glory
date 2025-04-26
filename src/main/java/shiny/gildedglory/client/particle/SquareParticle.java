package shiny.gildedglory.client.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import shiny.gildedglory.client.particle.effect.ColoredParticleEffect;

public class SquareParticle extends SpriteBillboardParticle {

    private final SpriteProvider spriteProvider;

    //Try and make these REALLY glow (using some sort of bloom)

    public SquareParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, ColoredParticleEffect parameters, SpriteProvider spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);

        this.ascending = true;
        this.spriteProvider = spriteProvider;

        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.velocityZ = velocityZ;

        this.red = parameters.getColor().x();
        this.green = parameters.getColor().y();
        this.blue = parameters.getColor().z();
        this.scale = parameters.getScale();

        this.maxAge = (int) (40 + this.random.nextInt(10) * Math.max(parameters.getScale(), 1.0f));

        this.setSpriteForAge(spriteProvider);
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteForAge(this.spriteProvider);
    }

    @Override
    public void move(double dx, double dy, double dz) {
        this.setBoundingBox(this.getBoundingBox().offset(dx, dy, dz));
        this.repositionFromBoundingBox();
    }

    @Override
    public int getBrightness(float tint) {
        return 15728880;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_LIT;
    }

    public static class Factory implements ParticleFactory<ColoredParticleEffect> {

        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(ColoredParticleEffect dustParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return new SquareParticle(clientWorld, d, e, f, g, h, i, dustParticleEffect, this.spriteProvider);
        }
    }
}