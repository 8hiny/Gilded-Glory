package shiny.gildedglory;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.ModelIdentifier;
import shiny.gildedglory.client.ModModelPredicateProviders;
import shiny.gildedglory.client.events.ClientEvents;
import shiny.gildedglory.client.particle.*;
import shiny.gildedglory.client.render.IraedeusEntityRenderer;
import shiny.gildedglory.common.registry.block.ModBlocks;
import shiny.gildedglory.common.registry.block.entity.ModBlockEntities;
import shiny.gildedglory.common.registry.entity.ModEntities;
import shiny.gildedglory.client.render.FramedChestBlockEntityRenderer;
import shiny.gildedglory.client.render.SlashEntityRenderer;

import java.util.UUID;

import static shiny.gildedglory.common.registry.particle.ModParticles.*;

public class GildedGloryClient implements ClientModInitializer {

    //ModelIdentifiers for items with seperate models
    public static final ModelIdentifier AURADEUS_GUI = new ModelIdentifier(GildedGlory.MOD_ID, "gui/auradeus", "inventory");
    public static final ModelIdentifier SWORDSPEAR_GUI = new ModelIdentifier(GildedGlory.MOD_ID, "gui/swordspear", "inventory");
    public static final ModelIdentifier IRAEDEUS_GUI = new ModelIdentifier(GildedGlory.MOD_ID, "gui/iraedeus", "inventory");
    public static final ModelIdentifier DR_PEPPER_GUI = new ModelIdentifier(GildedGlory.MOD_ID, "gui/dr_pepper", "inventory");
    public static final ModelIdentifier KATANA_GUI = new ModelIdentifier(GildedGlory.MOD_ID, "gui/katana", "inventory");

    //Custom keybinds
    public static KeyBinding returnIraedeus;
    public static KeyBinding targetIraedeus;

    //UUID'S for cosmetic particle spawning
    public static final UUID SHINY_UUID = UUID.fromString("a9bcfe9b-bb80-463d-848e-11e0b03f2b6e");

   @Override
   public void onInitializeClient() {
       ClientTickEvents.END_CLIENT_TICK.register(ClientEvents::clientTick);

       ModModelPredicateProviders.registerModelPredicateProviders();

       EntityModelLayerRegistry.registerModelLayer(FramedChestBlockEntityRenderer.SINGLE_MODEL_LAYER, FramedChestBlockEntityRenderer::getSingleTexturedModelData);
       EntityModelLayerRegistry.registerModelLayer(FramedChestBlockEntityRenderer.DOUBLE_MODEL_LAYER, FramedChestBlockEntityRenderer::getDoubleTexturedModelData);

       registerModParticles();
       registerModRenderers();
       registerKeybinds();
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
        ParticleFactoryRegistry.getInstance().register(SQUARE, SquareParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(SHOCKWAVE, ShockwaveParticle.Factory::new);
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

        //Try rendering custom objects at specified positions
        //WorldRenderEvents.AFTER_ENTITIES.register(TestManager::tick);

        BlockEntityRendererFactories.register(ModBlockEntities.FRAMED_CHEST, FramedChestBlockEntityRenderer::new);

        BuiltinItemRendererRegistry.INSTANCE.register(ModBlocks.FRAMED_CHEST, (stack, mode, matrixStack, vertexConsumers, light, overlay) ->
                FramedChestBlockEntityRenderer.renderItem(matrixStack, vertexConsumers, light, overlay)
        );
    }

    public static void registerKeybinds() {
       returnIraedeus = KeyBindingHelper.registerKeyBinding(new KeyBinding("keybind.gildedglory.iraedeus_return", InputUtil.UNKNOWN_KEY.getCode(), "key.categories.gildedglory"));
        targetIraedeus = KeyBindingHelper.registerKeyBinding(new KeyBinding("keybind.gildedglory.iraedeus_target", InputUtil.UNKNOWN_KEY.getCode(), "key.categories.gildedglory"));
    }
}
