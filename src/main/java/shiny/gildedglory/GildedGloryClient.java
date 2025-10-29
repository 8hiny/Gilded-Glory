package shiny.gildedglory;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.item.Item;
import org.ladysnake.satin.api.event.ShaderEffectRenderCallback;
import org.ladysnake.satin.api.managed.ManagedFramebuffer;
import org.ladysnake.satin.api.managed.ManagedShaderEffect;
import org.ladysnake.satin.api.managed.ShaderEffectManager;
import shiny.gildedglory.client.ModModelPredicateProviders;
import shiny.gildedglory.client.pose.CustomArmPoses;
import shiny.gildedglory.client.render.ModShaders;
import shiny.gildedglory.client.event.ClientEvents;
import shiny.gildedglory.client.particle.*;
import shiny.gildedglory.client.particle.custom.SimpleColoredParticle;
import shiny.gildedglory.client.render.ModShaderPrograms;
import shiny.gildedglory.client.render.entity.IraedeusEntityRenderer;
import shiny.gildedglory.client.sound.DynamicSounds;
import shiny.gildedglory.common.network.ModNetworking;
import shiny.gildedglory.common.registry.block.ModBlocks;
import shiny.gildedglory.common.registry.block.entity.ModBlockEntities;
import shiny.gildedglory.common.registry.entity.ModEntities;
import shiny.gildedglory.client.render.blockentity.FramedChestBlockEntityRenderer;
import shiny.gildedglory.client.render.entity.SlashEntityRenderer;
import shiny.gildedglory.client.use_action.CustomUseActions;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static shiny.gildedglory.common.registry.particle.ModParticles.*;

public class GildedGloryClient implements ClientModInitializer {

    //ModelIdentifiers for items with seperate models
    public static final Map<Item, ModelIdentifier> guiModels = new HashMap<>();

    //Custom keybinds
    public static KeyBinding returnIraedeus;
    public static KeyBinding targetIraedeus;

    //UUID'S for cosmetic particle spawning
    public static final UUID SHINY_UUID = UUID.fromString("a9bcfe9b-bb80-463d-848e-11e0b03f2b6e");

    //Post shader
    public static final ManagedShaderEffect GOLDEN_SHINE = ShaderEffectManager.getInstance().manage(GildedGlory.id("shaders/post/golden_shine.json"));
    public static final ManagedFramebuffer goldenShineBuffer = GOLDEN_SHINE.getTarget("final");

   @Override
   public void onInitializeClient() {
       ModModelPredicateProviders.registerModelPredicateProviders();
       ModShaderPrograms.registerModShaderPrograms();

       EntityModelLayerRegistry.registerModelLayer(FramedChestBlockEntityRenderer.SINGLE_MODEL_LAYER, FramedChestBlockEntityRenderer::getSingleTexturedModelData);
       EntityModelLayerRegistry.registerModelLayer(FramedChestBlockEntityRenderer.DOUBLE_MODEL_LAYER, FramedChestBlockEntityRenderer::getDoubleTexturedModelData);

       registerModParticles();
       registerModRenderers();
       registerModKeybinds();

       ClientEvents.clientInit();

       ModShaders.getInstance().init();
       CustomArmPoses.registerCustomArmPoses();
       CustomUseActions.registerCustomUseActions();
       DynamicSounds.registerDynamicSounds();
       ModNetworking.registerModClientReceivers();

       ClientTickEvents.END_CLIENT_TICK.register(ClientEvents::clientTick);

       //Golden Shine rendering
       ShaderEffectRenderCallback.EVENT.register(tickDelta -> {
           MinecraftClient client = MinecraftClient.getInstance();
           RenderSystem.enableBlend();
           RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
           goldenShineBuffer.draw(client.getWindow().getFramebufferWidth(), client.getWindow().getFramebufferHeight(), false);
           GOLDEN_SHINE.render(tickDelta);
           goldenShineBuffer.clear();
           RenderSystem.disableBlend();
           client.getFramebuffer().beginWrite(true);
       });
   }

    public static void registerModParticles() {
        ParticleFactoryRegistry.getInstance().register(SPARKLE, SparkleParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(SWIRL, SwirlParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ALERT, AlertParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(GOLD_SLASH, SlashParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(TWISTEEL_SLASH, SlashParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(IRAEDEUS_SLASH, SlashParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(GOLD_VERTICAL_SLASH, SlashParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(TWISTEEL_VERTICAL_SLASH, SlashParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(IRAEDEUS_VERTICAL_SLASH, SlashParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(SHOCKWAVE, ShockwaveParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(LARGE_IRAEDEUS_SLASH, LargeSlashParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(SQUARE, SquareParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(SHINE, SimpleColoredParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(SHINE_ANIMATED, EntityShineParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(SWORDSPEAR_SHINE, SwordSpearShineParticle.Factory::new);
    }

    public static void registerModRenderers() {
        EntityRendererRegistry.register(ModEntities.SLASH_PROJECTILE, SlashEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.IRAEDEUS, IraedeusEntityRenderer::new);

        //Scrapped feature
//        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper, context) -> {
//            if (entityType == EntityType.PLAYER) {
//                registrationHelper.register(new SummonedIraedeusFeatureRenderer(entityRenderer, context.getItemRenderer()));
//            }
//        });

        BlockEntityRendererFactories.register(ModBlockEntities.FRAMED_CHEST, FramedChestBlockEntityRenderer::new);

        BuiltinItemRendererRegistry.INSTANCE.register(ModBlocks.FRAMED_CHEST, (stack, mode, matrixStack, vertexConsumers, light, overlay) ->
                FramedChestBlockEntityRenderer.renderItem(matrixStack, vertexConsumers, light, overlay)
        );
    }

    public static void registerModKeybinds() {
       returnIraedeus = KeyBindingHelper.registerKeyBinding(new KeyBinding("keybind.gildedglory.iraedeus_return", InputUtil.UNKNOWN_KEY.getCode(), "key.categories.gildedglory"));
        targetIraedeus = KeyBindingHelper.registerKeyBinding(new KeyBinding("keybind.gildedglory.iraedeus_target", InputUtil.UNKNOWN_KEY.getCode(), "key.categories.gildedglory"));
    }
}
