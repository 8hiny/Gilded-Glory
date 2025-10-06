package shiny.gildedglory.client.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.awt.*;

public class GildedGloryUtil {

    public static void addPersonalParticles(Entity entity, ParticleEffect parameters, double x, double y, double z, double dx, double dy, double dz) {
        MinecraftClient client = MinecraftClient.getInstance();

        if ((!client.options.getPerspective().isFirstPerson() || client.player != entity) && !entity.isInvisibleTo(client.player)) {
            client.particleManager.addParticle(parameters, x, y, z, dx, dy, dz);
        }
    }

    /**
     * Draws a quad at the top position matrix of the MatrixStack with an offset, accepts textures with multiple frames stacked above each other.
     * @param width The width of the quad
     * @param height The height of the quad
     * @param frameHeight The height of all frames in the texture
     * @param frameWidth The width of all frames in the texture
     * @param textureHeight The height of the texture
     * @param textureWidth The width of the texture
     * @param frame The current frame with a minimum of 1
     */
    public static void drawQuad(
            MatrixStack matrices,
            VertexConsumer vertexConsumer,
            Vec3d position,
            float width, float height,
            int frameHeight, int frameWidth,
            int textureHeight, int textureWidth,
            int frame,
            int alpha,
            int light
    ) {
        MatrixStack.Entry entry = matrices.peek();
        Matrix4f positionMatrix = entry.getPositionMatrix();
        if (frame < 1) frame = 1;

        float xOffset = width / 2;
        float zOffset = height / 2;
        float uOffset = (float) frameWidth / textureWidth;
        float vOffset = (float) frameHeight / textureHeight;

        //Vertices in clockwise order, starting at top left
        vertexConsumer.vertex(positionMatrix,  (float) (position.x - xOffset), (float) position.y, (float) (position.z - zOffset))
                .color(255, 255, 255, alpha)
                .texture(0, vOffset * (frame - 1))
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(light)
                .normal(entry, 0, 1, 0);
        vertexConsumer.vertex(positionMatrix,(float) (position.x + xOffset), (float) position.y, (float) (position.z - zOffset))
                .color(255, 255, 255, alpha)
                .texture(uOffset, vOffset * (frame - 1))
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(light)
                .normal(entry, 0, 1, 0);
        vertexConsumer.vertex(positionMatrix,(float) (position.x + xOffset), (float) position.y,(float) (position.z + zOffset))
                .color(255, 255, 255, alpha)
                .texture(uOffset, vOffset * frame)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(light)
                .normal(entry, 0, 1, 0);
        vertexConsumer.vertex(positionMatrix, (float) (position.x - xOffset),  (float) position.y, (float) (position.z + zOffset))
                .color(255, 255, 255, alpha)
                .texture(0, vOffset * frame)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(light)
                .normal(entry, 0, 1, 0);
    }
}
