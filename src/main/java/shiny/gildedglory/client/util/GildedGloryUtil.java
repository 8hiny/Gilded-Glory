package shiny.gildedglory.client.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import shiny.gildedglory.GildedGlory;
import shiny.gildedglory.mixin.client.ClientPlayerEntityAccessor;
import team.lodestar.lodestone.registry.client.LodestoneRenderTypeRegistry;
import team.lodestar.lodestone.systems.rendering.VFXBuilders;
import team.lodestar.lodestone.systems.rendering.rendeertype.RenderTypeToken;
import team.lodestar.lodestone.systems.rendering.trail.TrailPoint;

import java.awt.*;
import java.util.List;

public class GildedGloryUtil {

    private static final RenderLayer TRAIL = LodestoneRenderTypeRegistry.ADDITIVE_TEXTURE_TRIANGLE.apply(RenderTypeToken.createToken(GildedGlory.id("textures/entity/trail.png")));

    public static void addPersonalParticles(Entity entity, ParticleEffect parameters, double x, double y, double z, double dx, double dy, double dz) {
        MinecraftClient client = MinecraftClient.getInstance();

        if ((!client.options.getPerspective().isFirstPerson() || client.player != entity) && !entity.isInvisibleTo(client.player)) {
            client.particleManager.addParticle(parameters, x, y, z, dx, dy, dz);
        }
    }

    public static void addPersonalParticles(Entity entity, ParticleEffect parameters, double x, double y, double z, double dx, double dy, double dz, ClientPlayerEntity... viewers) {
        for (ClientPlayerEntity player : viewers) {
            MinecraftClient client = ((ClientPlayerEntityAccessor) player).gildedglory$getClient();

            if ((!client.options.getPerspective().isFirstPerson() || player != entity) && !entity.isInvisibleTo(client.player)) {
                client.particleManager.addParticle(parameters, x, y, z, dx, dy, dz);
            }
        }
    }

    public static void addTrail(MatrixStack matrices, List<TrailPoint> points, Entity entity, Color color, float alpha, float size) {
        matrices.push();
        VFXBuilders.WorldVFXBuilder builder = VFXBuilders.createWorld().setRenderType(TRAIL);

        Vec3d camPos = MinecraftClient.getInstance().gameRenderer.getCamera().getPos();

        matrices.translate(-camPos.x, -camPos.y, -camPos.z);
        matrices.translate(0.0f, 0.0f, 0.0f);
        builder.setColor(color)
                .setAlpha(1.0f)
                .renderTrail(matrices,
                        points,
                        f -> MathHelper.sqrt(f) * size,
                        f -> builder.setAlpha((float) Math.cbrt(Math.max(0, (alpha * f) - 0.1f)))
                );
        matrices.pop();
    }

    /**
     * Draws a quad at the top position matrix of the MatrixStack with an offset, accepts textures with multiple frames stacked above each other.
     * @param offset The vector along which the quad should be offset from the position matrix
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
            Vec3d offset,
            float width, float height,
            int frameHeight, int frameWidth,
            int textureHeight, int textureWidth,
            int frame,
            int alpha,
            int light
    ) {
        Matrix4f positionMatrix = matrices.peek().getPositionMatrix();
        Matrix3f normalMatrix = matrices.peek().getNormalMatrix();
        if (frame < 1) frame = 1;

        float xOffset = width / 2;
        float zOffset = height / 2;
        float uOffset = (float) frameWidth / textureWidth;
        float vOffset = (float) frameHeight / textureHeight;

        //Vertices in clockwise order, starting at top left
        vertexConsumer.vertex(positionMatrix,  (float) (-xOffset + offset.x), (float) offset.y, (float) (-zOffset + offset.z))
                .color(255, 255, 255, alpha)
                .texture(0, vOffset * (frame - 1))
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(light)
                .normal(normalMatrix, 0, 1, 0)
                .next();
        vertexConsumer.vertex(positionMatrix,(float) (xOffset + offset.x), (float) offset.y, (float) (-zOffset + offset.z))
                .color(255, 255, 255, alpha)
                .texture(uOffset, vOffset * (frame - 1))
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(light)
                .normal(normalMatrix, 0, 1, 0)
                .next();
        vertexConsumer.vertex(positionMatrix,(float) (xOffset + offset.x), (float) offset.y,(float) (zOffset + offset.z))
                .color(255, 255, 255, alpha)
                .texture(uOffset, vOffset * frame)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(light)
                .normal(normalMatrix, 0, 1, 0)
                .next();
        vertexConsumer.vertex(positionMatrix, (float) (-xOffset + offset.x),  (float) offset.y, (float) (zOffset + offset.z))
                .color(255, 255, 255, alpha)
                .texture(0, vOffset * frame)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(light)
                .normal(normalMatrix, 0, 1, 0)
                .next();
    }
}
