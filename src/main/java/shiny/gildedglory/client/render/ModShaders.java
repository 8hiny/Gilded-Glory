package shiny.gildedglory.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import ladysnake.satin.api.event.EntitiesPostRenderCallback;
import ladysnake.satin.api.event.ResolutionChangeCallback;
import ladysnake.satin.api.event.ShaderEffectRenderCallback;
import ladysnake.satin.api.managed.ManagedShaderEffect;
import ladysnake.satin.api.managed.ShaderEffectManager;
import ladysnake.satin.api.managed.uniform.SamplerUniformV2;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import shiny.gildedglory.GildedGlory;
import shiny.gildedglory.client.slashed_area.SlashedAreaManager;

public class ModShaders implements EntitiesPostRenderCallback, WorldRenderEvents.AfterEntities, ResolutionChangeCallback, ShaderEffectRenderCallback {

    private final MinecraftClient client = MinecraftClient.getInstance();
    private final SlashedAreaManager slashedAreaManager = SlashedAreaManager.getInstance();
    private static ModShaders instance;

    //Post Shaders
    public ManagedShaderEffect MIRROR = ShaderEffectManager.getInstance().manage(GildedGlory.id("shaders/post/mirror.json"));

    //Frame Buffers
    public Framebuffer mirrorFrameBuffer = MIRROR.getTarget("final").getFramebuffer();

    //Uniforms
    public SamplerUniformV2 mirrorSampler = MIRROR.findSampler("MirrorSampler");

    private ModShaders() {
    }

    public static ModShaders getInstance() {
        if (instance == null) {
            instance = new ModShaders();
        }
        return instance;
    }

    public void init() {
        EntitiesPostRenderCallback.EVENT.register(this);
        WorldRenderEvents.AFTER_ENTITIES.register(this);
        ResolutionChangeCallback.EVENT.register(this);
        ShaderEffectRenderCallback.EVENT.register(this);
    }

    @Override
    public void onResolutionChanged(int newWidth, int newHeight) {
        if (this.mirrorFrameBuffer != null) {
            this.mirrorFrameBuffer.resize(newWidth, newHeight, MinecraftClient.IS_SYSTEM_MAC);
        }
    }

    //Runs right before block entities are rendered
    @Override
    public void onEntitiesRendered(Camera camera, Frustum frustum, float tickDelta) {
        if (this.mirrorFrameBuffer == null) {
            this.mirrorFrameBuffer = new SimpleFramebuffer(this.client.getWindow().getWidth(), this.client.getWindow().getHeight(),true, MinecraftClient.IS_SYSTEM_MAC);
        }
        this.mirrorFrameBuffer.clear(MinecraftClient.IS_SYSTEM_MAC);
        this.client.getFramebuffer().beginWrite(false);
    }

    //Runs right before block entities are rendered
    @Override
    public void afterEntities(WorldRenderContext context) {
    }

    //Runs when entity outlines are rendered (AFTER world rendering has finished)
    @Override
    public void renderShaderEffects(float tickDelta) {
        if (!this.slashedAreaManager.isEmpty() && this.mirrorFrameBuffer != null) {
            this.mirrorSampler.set(this.mirrorFrameBuffer.getColorAttachment());
            this.drawMirrorFramebuffer();
            this.MIRROR.render(tickDelta);
        }
    }

    public Framebuffer getMirrorFramebuffer() {
        return this.mirrorFrameBuffer;
    }

    public void drawMirrorFramebuffer() {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);

        this.mirrorFrameBuffer.draw(this.client.getWindow().getWidth(), this.client.getWindow().getHeight(), false);

        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
    }
}
