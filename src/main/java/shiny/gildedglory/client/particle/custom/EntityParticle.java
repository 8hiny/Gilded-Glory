package shiny.gildedglory.client.particle.custom;

import net.minecraft.client.particle.AnimatedParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import shiny.gildedglory.client.particle.effect.ColoredEntityParticleEffect;

public class EntityParticle extends AnimatedParticle {

    private final Entity entity;

    public EntityParticle(ClientWorld world, double x, double y, double z, ColoredEntityParticleEffect parameters, SpriteProvider spriteProvider) {
        super(world, x, y, z, spriteProvider, 0);

        this.entity = world.getEntityById(parameters.getEntityId());
        this.velocityX = 0.0;
        this.velocityY = 0.0;
        this.velocityZ = 0.0;

        this.red = parameters.getColor().x();
        this.green = parameters.getColor().y();
        this.blue = parameters.getColor().z();

        this.maxAge = parameters.getDuration();
        this.scale = parameters.getScale();

        this.setSpriteForAge(spriteProvider);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.entity != null) {
            if (!this.dead && !this.entity.isAlive()) {
                this.markDead();
            }

            Vec3d velocity = this.entity.getVelocity();
            this.x = this.entity.getX();
            this.y = this.entity.getY() + this.entity.getHeight() / 2;
            this.z = this.entity.getZ();

            this.velocityX = velocity.x;
            this.velocityY = velocity.y;
            this.velocityZ = velocity.z;
        }
        else {
            this.markDead();
        }
    }

    public static class Factory implements ParticleFactory<ColoredEntityParticleEffect> {

        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(ColoredEntityParticleEffect parameters, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return new EntityParticle(clientWorld, d, e, f, parameters, this.spriteProvider);
        }
    }
}
