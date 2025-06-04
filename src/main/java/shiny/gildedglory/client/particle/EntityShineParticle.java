package shiny.gildedglory.client.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import shiny.gildedglory.client.particle.custom.EntityParticle;
import shiny.gildedglory.client.particle.effect.ColoredEntityParticleEffect;

public class EntityShineParticle extends EntityParticle {

    private int frame = 0;

    public EntityShineParticle(ClientWorld world, double x, double y, double z, ColoredEntityParticleEffect parameters, SpriteProvider spriteProvider) {
        super(world, x, y, z, parameters, spriteProvider);
    }

    @Override
    public void tick() {
        super.tick();
        if (frame < 7) frame++;
        else frame = 0;

        this.setSprite(this.spriteProvider.getSprite(this.frame, 7));
    }

    public static class Factory implements ParticleFactory<ColoredEntityParticleEffect> {

        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(ColoredEntityParticleEffect parameters, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return new EntityShineParticle(clientWorld, d, e, f, parameters, this.spriteProvider);
        }
    }
}
