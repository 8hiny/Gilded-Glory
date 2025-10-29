package shiny.gildedglory.client.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import org.joml.Quaternionf;
import shiny.gildedglory.client.particle.custom.DirectionalParticle;
import shiny.gildedglory.client.particle.effect.VectorParticleEffect;
import shiny.gildedglory.common.util.GildedGloryUtil;

public class LargeSlashParticle extends DirectionalParticle {

    private final float angle;

    public LargeSlashParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, VectorParticleEffect parameters, SpriteProvider spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ, parameters, spriteProvider);
        this.scale = parameters.scale();
        this.maxAge = parameters.duration();

        this.angle = GildedGloryUtil.random(-60, 60) * (float) Math.PI / 180;
    }

    //TODO Slash particles are somehow flipped 180 degrees on the Y axis (backwards texture)
    @Override
    protected void applyRotations(Quaternionf quaternion) {
        quaternion.rotateX(this.angle + (float) Math.PI / 2);
        quaternion.rotateY(this.angle + (float) Math.PI / 2);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_LIT;
    }

    public static class Factory implements ParticleFactory<VectorParticleEffect> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(VectorParticleEffect parameters, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return new LargeSlashParticle(clientWorld, d, e, f, g, h, i, parameters, this.spriteProvider);
        }
    }
}
