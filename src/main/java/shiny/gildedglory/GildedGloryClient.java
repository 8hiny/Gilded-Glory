package shiny.gildedglory;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleEffect;
import org.jetbrains.annotations.Nullable;
import org.ladysnake.satin.api.managed.ManagedFramebuffer;
import org.ladysnake.satin.api.managed.ManagedShaderEffect;
import org.ladysnake.satin.api.managed.ShaderEffectManager;
import org.ladysnake.satin.api.managed.uniform.Uniform1f;
import shiny.gildedglory.client.ModModelPredicateProviders;
import shiny.gildedglory.client.pose.CustomArmPoses;
import shiny.gildedglory.client.event.ClientEvents;
import shiny.gildedglory.client.particle.*;
import shiny.gildedglory.client.particle.custom.SimpleColoredParticle;
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
import java.util.Set;
import java.util.UUID;

import static shiny.gildedglory.common.registry.particle.ModParticles.*;

public class GildedGloryClient implements ClientModInitializer {

    //ModelIdentifiers for items with seperate models
    public static final Map<Item, ModelIdentifier> guiModels = new HashMap<>();

    //Custom keybinds
    public static KeyBinding returnIraedeus;
    public static KeyBinding targetIraedeus;

    //Map of players to grant client-side particles
    public static final Map<UUID, ParticleEffect> playerParticles = new HashMap<>();

    //Me!
    public static final UUID SHINY_UUID = UUID.fromString("a9bcfe9b-bb80-463d-848e-11e0b03f2b6e");

    //Post shader stuff
    public static final ManagedShaderEffect GOLDEN_SHINE = ShaderEffectManager.getInstance().manage(GildedGlory.id("shaders/post/golden_shine.json"));
    public static final ManagedFramebuffer SHINE_BUFFER = GOLDEN_SHINE.getTarget("final");
    public static final Uniform1f WORLD_TIME = GOLDEN_SHINE.findUniform1f("WorldTime");

   @Override
   public void onInitializeClient() {
       ModModelPredicateProviders.registerModelPredicateProviders();

       EntityModelLayerRegistry.registerModelLayer(FramedChestBlockEntityRenderer.SINGLE_MODEL_LAYER, FramedChestBlockEntityRenderer::getSingleTexturedModelData);
       EntityModelLayerRegistry.registerModelLayer(FramedChestBlockEntityRenderer.DOUBLE_MODEL_LAYER, FramedChestBlockEntityRenderer::getDoubleTexturedModelData);

       registerModParticles();
       registerModRenderers();
       registerModKeybinds();

       ClientEvents.clientInit();

       CustomArmPoses.registerCustomArmPoses();
       CustomUseActions.registerCustomUseActions();
       DynamicSounds.registerDynamicSounds();
       ModNetworking.registerModClientReceivers();

       registerPlayerParticles();
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

    public static void registerPlayerParticles() {
       playerParticles.putIfAbsent(SHINY_UUID, SPARKLE);
    }

    public static Set<UUID> getPlayersWithParticles() {
       return playerParticles.keySet();
    }

    public static @Nullable ParticleEffect getPlayerParticle(UUID uuid) {
       return playerParticles.getOrDefault(uuid, null);
    }
}
