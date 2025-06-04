package shiny.gildedglory.client.render.entity;

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

public class SlashEntityRenderer extends EntityRenderer<SlashProjectileEntity> {

    public static final Identifier TEXTURE = GildedGlory.id("textures/entity/slash.png");
    public static final Identifier ALT_TEXTURE = GildedGlory.id("textures/entity/alternate_slash.png");
    private int frame = 1;

    public SlashEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    //Reduce frame speed (issue: frames are in relation to the class, not an instance or something, idk why they speed up when multiple slashes are present)

    @Override
    public void render(SlashProjectileEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        VertexConsumer vertexConsumer;
        if (entity.getVariant() == 0) vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(TEXTURE));
        else vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(ALT_TEXTURE));

        this.frame++;
        if (this.frame > 40) this.frame = 1;

        matrices.push();

        if (entity.isVertical()) {
            matrices.translate(0, 0.5f, 0);
        }
        else {
            matrices.translate(0, 0.25f, 0);
        }

        matrices.multiply(RotationAxis.POSITIVE_Y.rotation((float) (Math.PI + (entity.getYaw() * Math.PI / 180))));
        matrices.multiply(RotationAxis.POSITIVE_X.rotation((float) (entity.getPitch() * Math.PI / 180)));

        if (entity.isVertical()) {
            matrices.multiply(RotationAxis.POSITIVE_Z.rotation((float) Math.PI / 2));
        }

        GildedGloryUtil.drawQuad(matrices, vertexConsumer, Vec3d.ZERO, 2.4f, 1.6f, 32, 48, 128, 48, this.frame / 10, 255, 15728880);

        //Draws two slashes in an X formation, unsure about how it looks though
//        matrices.push();
//        matrices.multiply(RotationAxis.POSITIVE_Z.rotation((float) (22.5 * Math.PI / 180)));
//        GildedGloryUtil.drawQuad(matrices, vertexConsumer, Vec3d.ZERO, 2.4f, 1.6f, 32, 48, 128, 48, this.frame / 10, 255, 15728880);
//        matrices.pop();
//
//        matrices.multiply(RotationAxis.POSITIVE_Z.rotation((float) (-22.5 * Math.PI / 180)));
//        GildedGloryUtil.drawQuad(matrices, vertexConsumer, Vec3d.ZERO, 2.4f, 1.6f, 32, 48, 128, 48, this.frame / 10, 255, 15728880);

        matrices.pop();
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public Identifier getTexture(SlashProjectileEntity entity) {
        return entity.getVariant() == 0 ? TEXTURE : ALT_TEXTURE;
    }

    protected int getBlockLight(SlashProjectileEntity entity, BlockPos blockPos) {
        return 15;
    }
}
