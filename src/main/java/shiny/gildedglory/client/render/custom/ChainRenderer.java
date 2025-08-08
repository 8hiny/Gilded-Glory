package shiny.gildedglory.client.render.custom;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import shiny.gildedglory.GildedGlory;
import shiny.gildedglory.client.util.GildedGloryUtil;

public class ChainRenderer {

    private static final Identifier TEXTURE = GildedGlory.id("textures/entity/chain.png");

    public static void render(Vec3d origin, Vec3d target, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider) {
        Vec3d direction = target.subtract(origin).normalize();
        double horizontalMagnitude = direction.horizontalLength();

        double angle = Math.acos(direction.x / horizontalMagnitude);
        if (direction.z > 0.0) angle = (Math.PI * 2 - angle);

        double angle1 = Math.atan(direction.y / horizontalMagnitude);

        int segments = (int) Math.ceil(origin.distanceTo(target) * 2);
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(TEXTURE, false));

        matrices.push();
        matrices.translate(0, 1, 0);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotation((float) angle));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotation((float) angle1));

        for (int i = 0; i < segments; i++) {
            int frame = Math.min(i + 1, 10);
            Vec3d vec = new Vec3d(0.48, 0, 0);

            if (i % 2 == 0) {
                matrices.multiply(RotationAxis.POSITIVE_X.rotation((float) (90 * Math.PI / 180)));
            }
            else {
                matrices.multiply(RotationAxis.POSITIVE_X.rotation((float) -(90 * Math.PI / 180)));
            }

            GildedGloryUtil.drawQuad(matrices, vertexConsumer, vec.multiply(i), 0.6f, 0.36f, 3, 5, 32, 32, frame, 255, 15728880);
        }
        matrices.pop();
    }
}
