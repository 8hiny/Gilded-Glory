package shiny.gildedglory.client.render.custom;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import shiny.gildedglory.GildedGlory;
import shiny.gildedglory.client.render.ModRenderLayers;

public class ChainRenderer {

    private static final Identifier TEXTURE = GildedGlory.id("textures/entity/chain.png");

    public static void render(Vec3d origin, Vec3d target, Vec3d camPos, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider) {
        Vec3d direction = target.subtract(origin).normalize();

        int segments = (int) Math.ceil(origin.distanceTo(target) / 0.38);
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(ModRenderLayers.getEntityTranslucentGlowing(TEXTURE));

        matrices.push();
        matrices.translate(-camPos.x, -camPos.y, -camPos.z);

        for (int i = 0; i < segments; i++) {
            Vec3d pos = origin.add(direction.multiply(i).multiply(0.38));
            Vec3d offset = direction.multiply(0.24);
            Vec3d horizontal = direction.crossProduct(new Vec3d(0, 1, 0)).normalize().multiply(0.18);
            Vec3d vertical = direction.crossProduct(horizontal).normalize().multiply(0.18);

            Vec3d a;
            Vec3d b;
            Vec3d c;
            Vec3d d;
            if (i % 2 == 0) {
                a = pos.add(offset).add(vertical);
                b = pos.add(offset).subtract(vertical);
                c = pos.subtract(offset).add(vertical);
                d = pos.subtract(offset).subtract(vertical);
            }
            else {
                a = pos.add(offset).add(horizontal);
                b = pos.add(offset).subtract(horizontal);
                c = pos.subtract(offset).add(horizontal);
                d = pos.subtract(offset).subtract(horizontal);
            }

            MatrixStack.Entry entry = matrices.peek();
            int frame = Math.min((i + 1) / 2, 10);
            float uOffset = (float) 3 / 32;
            float vOffset = (float) 5 / 32;

            vertexConsumer.vertex(entry, (float) a.x, (float) a.y, (float) a.z)
                    .color(1.0f, 1.0f, 1.0f, 0.8f)
                    .texture(0, uOffset * (frame - 1))
                    .overlay(OverlayTexture.DEFAULT_UV)
                    .light(15728880)
                    .normal(entry, 0, 1, 0);
            vertexConsumer.vertex(entry, (float) b.x, (float) b.y, (float) b.z)
                    .color(1.0f, 1.0f, 1.0f, 0.8f)
                    .texture(0, uOffset * frame)
                    .overlay(OverlayTexture.DEFAULT_UV)
                    .light(15728880)
                    .normal(entry, 0, 1, 0);
            vertexConsumer.vertex(entry, (float) d.x, (float) d.y, (float) d.z)
                    .color(1.0f, 1.0f, 1.0f, 0.8f)
                    .texture(vOffset, uOffset * frame)
                    .overlay(OverlayTexture.DEFAULT_UV)
                    .light(15728880)
                    .normal(entry, 0, 1, 0);
            vertexConsumer.vertex(entry, (float) c.x, (float) c.y, (float) c.z)
                    .color(1.0f, 1.0f, 1.0f, 0.8f)
                    .texture(vOffset, uOffset * (frame - 1))
                    .overlay(OverlayTexture.DEFAULT_UV)
                    .light(15728880)
                    .normal(entry, 0, 1, 0);
        }
        matrices.pop();
    }
}
