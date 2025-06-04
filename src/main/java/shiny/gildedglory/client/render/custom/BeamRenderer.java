package shiny.gildedglory.client.render.custom;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import shiny.gildedglory.GildedGlory;
import shiny.gildedglory.client.render.ModRenderLayers;
import shiny.gildedglory.client.util.GildedGloryUtil;

public class BeamRenderer {

    public static final Identifier TEXTURE = GildedGlory.id("textures/entity/beam.png");

    public static void render(LivingEntity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, int light, boolean firstPerson) {
        float f = Math.floorMod(entity.getWorld().getTime(), 40);
        float length = 40.0f;
        float size = 0.5f;

        matrices.push();
        if (firstPerson) {
            length = 80.0f;
            size = 0.15f;

            matrices.multiply(RotationAxis.POSITIVE_Y.rotation((float) Math.PI));
            matrices.translate(0.5625f, 0.35f, -0.45f);
        }
        else {
            matrices.multiply(RotationAxis.POSITIVE_Y.rotation((float) (5.25 * Math.PI / 6)));
            matrices.translate(0.126f, -0.13f, 1.25f);
        }

        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(f * 2.25f - 45.0f));
        renderBeam(matrices, vertexConsumerProvider.getBuffer(ModRenderLayers.getBeam(TEXTURE, false)), length, size, 255, light);
        matrices.pop();

        renderBeam(matrices, vertexConsumerProvider.getBuffer(ModRenderLayers.getBeam(TEXTURE, true)), length, size * 1.5f, 96, light);
        matrices.pop();
    }

    public static void renderBeam(MatrixStack matrices, VertexConsumer vertexConsumer, float length, float size, int alpha, int light) {
        float max = length / size;
        for (int i = 0; i < max; i++) {
            Vec3d vec = new Vec3d(0, 0, size);

            if (max - i <= 20) {
                alpha = (int) (alpha * Math.max(0, (max - i) * 0.05f));
            }
            renderOpenCuboid(matrices, vertexConsumer, vec.multiply(i), size, alpha, light);
        }
    }

    public static void renderOpenCuboid(MatrixStack matrices, VertexConsumer vertexConsumer, Vec3d offset, float size, int alpha, int light) {
        float rightAngle = (float) (Math.PI / 2);

        //Bottom quad
        matrices.translate(0, -size / 2, 0);
        GildedGloryUtil.drawQuad(matrices, vertexConsumer, offset, size, size, 16, 16, 16, 16, 1, alpha, light);
        //Top quad
        matrices.translate(0, size, 0);
        GildedGloryUtil.drawQuad(matrices, vertexConsumer, offset, size, size, 16, 16, 16, 16, 1, alpha, light);

        matrices.multiply(RotationAxis.POSITIVE_Z.rotation(rightAngle));

        //Right vertical quad
        matrices.translate(-size / 2, size / 2, 0);
        GildedGloryUtil.drawQuad(matrices, vertexConsumer, offset, size, size, 16, 16, 16, 16, 1, alpha, light);
        //Left vertical quad
        matrices.translate(0, -size, 0);
        GildedGloryUtil.drawQuad(matrices, vertexConsumer, offset, size, size, 16, 16, 16, 16, 1, alpha, light);

        matrices.multiply(RotationAxis.POSITIVE_Z.rotation(-rightAngle));
        matrices.translate(-size / 2, 0, 0);
    }
}
