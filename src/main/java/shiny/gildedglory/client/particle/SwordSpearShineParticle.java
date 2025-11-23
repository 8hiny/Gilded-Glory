package shiny.gildedglory.client.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import shiny.gildedglory.client.particle.effect.ColoredEntityParticleEffect;
import shiny.gildedglory.client.util.GildedGloryClientUtil;
import shiny.gildedglory.common.item.custom.ChargeableWeapon;
import shiny.gildedglory.common.registry.item.ModItems;

public class SwordSpearShineParticle extends EntityShineParticle {

    public SwordSpearShineParticle(ClientWorld world, double x, double y, double z, ColoredEntityParticleEffect parameters, SpriteProvider spriteProvider) {
        super(world, x, y, z, parameters, spriteProvider);
        this.red = 1;
        this.blue = 1;
        this.green = 1;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.entity != null) {
            this.offset = this.entity.getRotationVector().multiply(1.4).add(0, this.entity.getHeight() * 0.75, 0).toVector3f();
            if (this.entity instanceof LivingEntity livingEntity) {
                ItemStack stack = livingEntity.getMainHandStack();
                if (!stack.isOf(ModItems.SWORDSPEAR) || !ChargeableWeapon.hasCharge(stack)) {
                    this.markDead();
                }
            }
        }
    }

    @Override
    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        if (GildedGloryClientUtil.notFirstPersonOrOtherEntity(this.entity)) {
            super.buildGeometry(vertexConsumer, camera, tickDelta);
        }
    }

    public static class Factory implements ParticleFactory<ColoredEntityParticleEffect> {

        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(ColoredEntityParticleEffect parameters, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return new SwordSpearShineParticle(clientWorld, d, e, f, parameters, this.spriteProvider);
        }
    }
}