package shiny.gildedglory.client.render.entity;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import shiny.gildedglory.GildedGlory;
import shiny.gildedglory.client.render.ModRenderLayers;
import shiny.gildedglory.common.entity.SlashProjectileEntity;
import shiny.gildedglory.client.util.GildedGloryClientUtil;

import java.awt.*;

public class SlashEntityRenderer extends EntityRenderer<SlashProjectileEntity> {

    public static final Identifier TEXTURE = GildedGlory.id("textures/entity/slash.png");
    public static final Identifier ALT_TEXTURE = GildedGlory.id("textures/entity/alternate_slash.png");

    public SlashEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(SlashProjectileEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();

        if (entity.isVertical()) matrices.translate(0, 0.5f, 0);
        else matrices.translate(0, 0.25f, 0);

        //Render pulsating "particle" for increased visibility
        matrices.push();
        matrices.multiply(MinecraftClient.getInstance().gameRenderer.getCamera().getRotation());
        matrices.translate(0, 0, 0.5f);

        int i = switch (entity.getShineFrame()) {
            case 4 -> 2;
            case 5 -> 1;
            case 6 -> 0;
            default -> entity.getShineFrame();
        };

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(ModRenderLayers.getEntityTranslucentGlowing(GildedGlory.id("textures/particle/shine_" + i + ".png")));
        drawQuad(matrices.peek(), vertexConsumer, Vec3d.ZERO, entity.getVariant() == 0 ? new Color(0.96f, 0.77f, 0.19f) : new Color(0.8f, 0.29f, 0.36f));
        matrices.pop();

        //Render actual slash quad
        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotation((float) (Math.PI + (entity.getYaw() * Math.PI / 180))));
        matrices.multiply(RotationAxis.POSITIVE_X.rotation((float) (entity.getPitch() * Math.PI / 180)));

        if (entity.isVertical()) {
            matrices.multiply(RotationAxis.POSITIVE_Z.rotation((float) Math.PI / 2));
        }

        VertexConsumer vertexConsumer1;
        if (entity.getVariant() == 0) vertexConsumer1 = vertexConsumers.getBuffer(ModRenderLayers.getSlash(TEXTURE));
        else vertexConsumer1 = vertexConsumers.getBuffer(ModRenderLayers.getSlash(ALT_TEXTURE));

        GildedGloryClientUtil.drawQuad(matrices, vertexConsumer1, Vec3d.ZERO, Color.WHITE, 2.4f, 1.6f, 32, 48, 128, 48, entity.getTextureFrame(), 255, 15728880);
        matrices.pop();

        matrices.pop();
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public Identifier getTexture(SlashProjectileEntity entity) {
        return entity.getVariant() == 0 ? TEXTURE : ALT_TEXTURE;
    }

    public static void drawQuad(MatrixStack.Entry entry, VertexConsumer vertexConsumer, Vec3d pos, Color color) {
        vertexConsumer.vertex(entry, (float) pos.x - 0.75f, (float) pos.y + 0.75f, (float) pos.z)
                .color(color.getRed(), color.getGreen(), color.getBlue(), 255)
                .texture(0, 0)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(15728880)
                .normal(entry, 0, 1, 0);
        vertexConsumer.vertex(entry, (float) pos.x + 0.75f, (float) pos.y + 0.75f, (float) pos.z)
                .color(color.getRed(), color.getGreen(), color.getBlue(), 255)
                .texture(1, 0)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(15728880)
                .normal(entry, 0, 1, 0);
        vertexConsumer.vertex(entry, (float) pos.x + 0.75f, (float) pos.y - 0.75f, (float) pos.z)
                .color(color.getRed(), color.getGreen(), color.getBlue(), 255)
                .texture(1, 1)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(15728880)
                .normal(entry, 0, 1, 0);
        vertexConsumer.vertex(entry, (float) pos.x - 0.75f, (float) pos.y - 0.75f, (float) pos.z)
                .color(color.getRed(), color.getGreen(), color.getBlue(), 255)
                .texture(0, 1)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(15728880)
                .normal(entry, 0, 1, 0);
    }
}
