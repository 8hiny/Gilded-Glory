package shiny.gildedglory.client.render;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import shiny.gildedglory.GildedGlory;
import shiny.gildedglory.common.entity.SlashProjectileEntity;
import shiny.gildedglory.client.util.GildedGloryUtil;

import java.awt.*;

public class SlashEntityRenderer extends EntityRenderer<SlashProjectileEntity> {

    public static final Identifier TEXTURE = GildedGlory.id("textures/entity/slash.png");
    public static final Identifier ALT_TEXTURE = GildedGlory.id("textures/entity/alternate_slash.png");
    private int frame = 1;

    public SlashEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    //Reduce frame speed (issue: frames are in relation to the class, not an instance or something, idk why they speed up when multiple slashes are present)

    @Override
    public void render(SlashProjectileEntity entity, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light) {
        VertexConsumer vertexConsumer;
        if (entity.getVariant() == 0) vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(TEXTURE));
        else vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(ALT_TEXTURE));

        this.frame++;
        if (this.frame > 40) this.frame = 1;

        matrixStack.push();

        if (entity.isVertical()) {
            matrixStack.translate(0, 0.5f, 0);
        }
        else {
            matrixStack.translate(0, 0.25f, 0);
        }

        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotation((float) (Math.PI + (entity.getYaw() * Math.PI / 180))));
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotation((float) (entity.getPitch() * Math.PI / 180)));

        if (entity.isVertical()) {
            matrixStack.multiply(RotationAxis.POSITIVE_Z.rotation((float) Math.PI / 2));
        }

        matrixStack.push();
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotation((float) (22.5 * Math.PI / 180)));
        GildedGloryUtil.drawQuad(matrixStack, vertexConsumer, Vec3d.ZERO, 2.4f, 1.6f, 32, 48, 128, 48, this.frame / 10, 255, 15728880);
        matrixStack.pop();

        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotation((float) (-22.5 * Math.PI / 180)));
        GildedGloryUtil.drawQuad(matrixStack, vertexConsumer, Vec3d.ZERO, 2.4f, 1.6f, 32, 48, 128, 48, this.frame / 10, 255, 15728880);
        matrixStack.pop();

        super.render(entity, yaw, tickDelta, matrixStack, vertexConsumerProvider, light);

        //Trail
        Color color = entity.getVariant() == 0 ? new Color(246, 197, 49) : new Color(204, 73, 92);
        GildedGloryUtil.addTrail(matrixStack, entity.getTrailPoints(), entity, color, 1.0f, 1.4f, tickDelta);
    }

    @Override
    public Identifier getTexture(SlashProjectileEntity entity) {
        return entity.getVariant() == 0 ? TEXTURE : ALT_TEXTURE;
    }

    protected int getBlockLight(SlashProjectileEntity entity, BlockPos blockPos) {
        return 15;
    }
}
