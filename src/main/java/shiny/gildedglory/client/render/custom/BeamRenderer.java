package shiny.gildedglory.client.render.custom;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import shiny.gildedglory.GildedGlory;
import shiny.gildedglory.client.render.ModRenderLayers;
import shiny.gildedglory.client.util.GildedGloryClientUtil;

import java.awt.*;

public class BeamRenderer {

    public static final Identifier BASE_TEXTURE = GildedGlory.id("textures/entity/beam/base.png");
    public static final Identifier GOLDEN_TEXTURE = GildedGlory.id("textures/entity/beam/golden.png");
    public static final Identifier RUBY_TEXTURE = GildedGlory.id("textures/entity/beam/ruby.png");

    public static void renderGold(
            World world,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            float length,
            float size,
            int light,
            boolean firstPerson,
            boolean rotateOuter
    ) {
        render(world, matrices, vertexConsumers, GOLDEN_TEXTURE, GOLDEN_TEXTURE, Color.WHITE, length, size, light, firstPerson, rotateOuter);
    }

    public static void renderRuby(
            World world,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            float length,
            float size,
            int light,
            boolean firstPerson,
            boolean rotateOuter
    ) {
        render(world, matrices, vertexConsumers, RUBY_TEXTURE, RUBY_TEXTURE, Color.WHITE, length, size, light, firstPerson, rotateOuter);
    }

    public static void render(
            World world,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            Identifier outerTexture,
            Identifier innerTexture,
            Color color,
            float length,
            float size,
            int light,
            boolean firstPerson,
            boolean rotateOuter
    ) {
        float f = Math.floorMod(world.getTime(), 40);

        matrices.push();
        if (firstPerson) {
            length *= 2.0f;
            size *= 0.3f;

            matrices.multiply(RotationAxis.POSITIVE_Y.rotation((float) Math.PI));
            matrices.translate(0.5625f, 0.35f, -0.45f);
        }
        else {
            matrices.multiply(RotationAxis.POSITIVE_Y.rotation((float) (5.25 * Math.PI / 6)));
            matrices.translate(0.126f, -0.13f, 1.25f);
        }

        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(f * 2.25f - 45.0f));
        renderBeam(matrices, vertexConsumers.getBuffer(ModRenderLayers.getBeam(outerTexture, false)), color, length, size, 255, light);
        matrices.pop();

        if (rotateOuter) matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(f * -2.25f + 45.0f));
        renderBeam(matrices, vertexConsumers.getBuffer(ModRenderLayers.getBeam(innerTexture, true)), color.brighter(), length, size * 1.5f, 96, light);
        matrices.pop();
    }

    public static void renderBeam(MatrixStack matrices, VertexConsumer vertexConsumer, Color color, float length, float size, int alpha, int light) {
        float max = length / size;
        for (int i = 0; i < max; i++) {
            Vec3d vec = new Vec3d(0, 0, size);

            if (max - i <= 20) {
                alpha = (int) (alpha * Math.max(0, (max - i) * 0.05f));
            }
            renderOpenCuboid(matrices, vertexConsumer, vec.multiply(i), color, size, alpha, light);
        }
    }

    public static void renderOpenCuboid(MatrixStack matrices, VertexConsumer vertexConsumer, Vec3d offset, Color color, float size, int alpha, int light) {
        float rightAngle = (float) (Math.PI / 2);

        //Bottom quad
        matrices.translate(0, -size / 2, 0);
        GildedGloryClientUtil.drawQuad(matrices, vertexConsumer, offset, color, size, size, 16, 16, 16, 16, 1, alpha, light);
        //Top quad
        matrices.translate(0, size, 0);
        GildedGloryClientUtil.drawQuad(matrices, vertexConsumer, offset, color, size, size, 16, 16, 16, 16, 1, alpha, light);

        matrices.multiply(RotationAxis.POSITIVE_Z.rotation(rightAngle));

        //Right vertical quad
        matrices.translate(-size / 2, size / 2, 0);
        GildedGloryClientUtil.drawQuad(matrices, vertexConsumer, offset, color, size, size, 16, 16, 16, 16, 1, alpha, light);
        //Left vertical quad
        matrices.translate(0, -size, 0);
        GildedGloryClientUtil.drawQuad(matrices, vertexConsumer, offset, color, size, size, 16, 16, 16, 16, 1, alpha, light);

        matrices.multiply(RotationAxis.POSITIVE_Z.rotation(-rightAngle));
        matrices.translate(-size / 2, 0, 0);
    }
}
