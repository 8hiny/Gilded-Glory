package shiny.gildedglory.mixin.client;

import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shiny.gildedglory.client.render.custom.ChainRenderer;
import shiny.gildedglory.common.registry.component.ModComponents;
import shiny.gildedglory.common.registry.item.ModItems;
import shiny.gildedglory.common.util.GildedGloryUtil;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements FeatureRendererContext<T, M> {

    protected LivingEntityRendererMixin(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "TAIL"))
    private void gildedglory$injectCustomRenderers(T entity, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, CallbackInfo ci) {
        if (ModComponents.CHAINED.get(entity).getDuration() > 0) {
            boolean bl = ModComponents.CHAINED.get(entity).isAttacker();

            Entity counterpart = GildedGloryUtil.getEntityClient(ModComponents.CHAINED.get(entity).getCounterpart(), (ClientWorld) entity.getWorld());
            if (counterpart != null && bl) {
                ChainRenderer.render(entity.getLerpedPos(tickDelta), counterpart.getLerpedPos(tickDelta), matrixStack, vertexConsumerProvider);
            }
        }
    }

    @Override
    public boolean shouldRender(T entity, Frustum frustum, double x, double y, double z) {
        if (super.shouldRender(entity, frustum, x, y, z)) {
            return true;
        }
        else {
            if (ModComponents.CHAINED.get(entity).getDuration() > 0 || entity.getMainHandStack().isOf(ModItems.SWORDSPEAR)) {
                Vec3d pos = entity.getLerpedPos(0.5f);
                Vec3d pos1 = null;

                if (ModComponents.CHAINED.get(entity).getDuration() > 0) {
                    Entity entity1 = GildedGloryUtil.getEntityFromUuid(ModComponents.CHAINED.get(entity).getCounterpart(), entity.getWorld());

                    if (entity1 != null) {
                        pos1 = entity1.getPos();
                    }
                }
                else {
                    pos1 = pos.add(entity.getRotationVec(1.0f).multiply(40.0f));
                }
                return pos1 != null && frustum.isVisible(new Box(pos.x, pos.y, pos.z, pos1.x, pos1.y, pos1.z));
            }
            return false;
        }
    }
}
