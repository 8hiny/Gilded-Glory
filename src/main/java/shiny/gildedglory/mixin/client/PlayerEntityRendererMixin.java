package shiny.gildedglory.mixin.client;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import shiny.gildedglory.GildedGloryClient;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {

    public PlayerEntityRendererMixin(EntityRendererFactory.Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Override
    protected @Nullable RenderLayer getRenderLayer(AbstractClientPlayerEntity entity, boolean showBody, boolean translucent, boolean showOutline) {
        RenderLayer baseLayer = super.getRenderLayer(entity, showBody, translucent, showOutline);
        if (baseLayer != null) {
            return GildedGloryClient.goldenShineBuffer.getRenderLayer(baseLayer);
        }
        return baseLayer;
    }
}
