package shiny.gildedglory.client.render.custom;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import shiny.gildedglory.GildedGlory;

import java.awt.*;

public class OverlayRenderer {

    public static final Identifier CROSSHAIR = GildedGlory.id("textures/gui/crosshair.png");
    public static final Identifier CHAINED_OVERLAY = GildedGlory.id("textures/misc/chained_overlay.png");
    public static final Identifier LINES_OVERLAY = GildedGlory.id("textures/misc/lines_vignette.png");

    public static void renderChainedOverlay(DrawContext context, int frame) {
        renderAnimatedOverlay(context, CHAINED_OVERLAY, 256, 1792, frame);
    }

    public static void renderIraedeusOverlay(DrawContext context, boolean controlling) {
        renderTintedOverlay(context, LINES_OVERLAY, new Color(74, 98, 106), controlling ? 0.4f : 0.1f);
    }

    public static void renderTintedOverlay(DrawContext context, Identifier texture, Color color, float alpha) {
        int width = context.getScaledWindowWidth();
        int height = context.getScaledWindowHeight();

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE);
        context.setShaderColor((float) color.getRed() / 255, (float) color.getGreen() / 255, (float) color.getBlue() / 255, alpha);

        context.drawTexture(texture, 0, 0, 0, 0, width, height, width, height);

        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static void renderAnimatedOverlay(DrawContext context, Identifier texture, int textureWidth, int textureHeight, int frame) {
        int width = context.getScaledWindowWidth();
        int height = context.getScaledWindowHeight();

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        context.drawTexture(texture, 0, 0, width, height, 0, frame, 256, 256, textureWidth, textureHeight);

        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }
}
