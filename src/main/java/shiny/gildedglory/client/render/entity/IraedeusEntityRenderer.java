package shiny.gildedglory.client.render.entity;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import shiny.gildedglory.client.util.GildedGloryUtil;
import shiny.gildedglory.common.entity.IraedeusEntity;

import java.awt.*;

public class IraedeusEntityRenderer extends EntityRenderer<IraedeusEntity> {

    private final ItemRenderer itemRenderer;

    public IraedeusEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.itemRenderer = ctx.getItemRenderer();
    }

    @Override
    public void render(IraedeusEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        float rotation = (entity.age + tickDelta) * 75;

        matrices.push();
        matrices.scale(1.25f, 1.25f, 1.25f);
        matrices.translate(0.0f, 0.2f, 0.0f);

        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(entity.getYaw(tickDelta) - 90.0f));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(entity.getPitch(tickDelta) + 90.0f));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0f));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotation));

        this.itemRenderer
                .renderItem(
                        entity.getStack(), ModelTransformationMode.FIXED, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, entity.getWorld(), entity.getId()
                );

        matrices.pop();
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);

        //Trail
        GildedGloryUtil.addTrail(matrices, entity.getTrailPoints(), entity, new Color(74, 98, 106), 1.0f, 1.1f);
    }

    @Override
    public Identifier getTexture(IraedeusEntity entity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
    }
}
