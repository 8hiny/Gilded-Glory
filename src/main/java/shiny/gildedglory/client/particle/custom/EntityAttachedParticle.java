package shiny.gildedglory.client.particle.custom;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;
import shiny.gildedglory.client.particle.effect.ColoredEntityParticleEffect;

public class EntityAttachedParticle extends AnimatedParticle {

    protected final Entity entity;
    protected Vector3f offset;

    public EntityAttachedParticle(ClientWorld world, double x, double y, double z, ColoredEntityParticleEffect parameters, SpriteProvider spriteProvider) {
        super(world, x, y, z, spriteProvider, 0);

        this.entity = world.getEntityById(parameters.entityId());
        this.offset = parameters.offset();
        this.updateStatus();

        this.red = parameters.color().x();
        this.green = parameters.color().y();
        this.blue = parameters.color().z();

        this.maxAge = parameters.duration();
        this.scale = parameters.scale();

        this.setSpriteForAge(spriteProvider);
    }

    @Override
    public void tick() {
        super.tick();
        this.updateStatus();
        if (this.entity == null) {
            this.markDead();
        }
    }

    public void updateStatus() {
        if (this.entity != null) {
            if (!this.dead && !this.entity.isAlive()) {
                this.markDead();
            }

            this.x = this.entity.getX() + this.offset.x;
            this.y = this.entity.getY() + this.offset.y;
            this.z = this.entity.getZ() + this.offset.z;

            Vec3d velocity = this.entity.getVelocity();
            this.velocityX = velocity.x;
            this.velocityY = velocity.y;
            this.velocityZ = velocity.z;
        }
    }

    public static class Factory implements ParticleFactory<ColoredEntityParticleEffect> {

        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(ColoredEntityParticleEffect parameters, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return new EntityAttachedParticle(clientWorld, d, e, f, parameters, this.spriteProvider);
        }
    }
}