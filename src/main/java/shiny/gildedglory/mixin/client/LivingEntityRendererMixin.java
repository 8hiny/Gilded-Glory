package shiny.gildedglory.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import shiny.gildedglory.GildedGloryClient;
import shiny.gildedglory.common.registry.component.ModComponents;
import shiny.gildedglory.common.registry.item.ModItems;
import shiny.gildedglory.common.util.GildedGloryUtil;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements FeatureRendererContext<T, M> {

    protected LivingEntityRendererMixin(EntityRendererFactory.Context ctx) {
        super(ctx);
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

    @WrapMethod(method = "getRenderLayer")
    private @Nullable RenderLayer gildedglory$blah(T entity, boolean showBody, boolean translucent, boolean showOutline, Operation<RenderLayer> original) {
        RenderLayer baseLayer = original.call(entity, showBody, translucent, showOutline);
        if (baseLayer != null && entity instanceof PlayerEntity && entity.getUuid().equals(GildedGloryClient.SHINY_UUID)) {
            return GildedGloryClient.goldenShineBuffer.getRenderLayer(baseLayer);
        }
        return baseLayer;
    }
}
