package shiny.gildedglory.client.event;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.random.Random;
import shiny.gildedglory.GildedGloryClient;
import shiny.gildedglory.client.render.custom.ChainRenderer;
import shiny.gildedglory.client.render.custom.OverlayRenderer;
import shiny.gildedglory.client.slashed_area.SlashedAreaManager;
import shiny.gildedglory.client.util.GildedGloryUtil;
import shiny.gildedglory.common.component.entity.ThrowableSwordComponent;
import shiny.gildedglory.common.item.custom.ChargeableWeapon;
import shiny.gildedglory.common.registry.component.ModComponents;
import shiny.gildedglory.common.registry.item.ModItems;
import shiny.gildedglory.common.registry.particle.ModParticles;
import shiny.gildedglory.common.util.DynamicSoundManager;

public class ClientEvents {

    private static int chainedTick = 0;

    public static void clientTick(MinecraftClient client) {
        DynamicSoundManager.getInstance().tick();
        SlashedAreaManager.getInstance().tick();
        ThrowableSwordComponent.clientTick(client);
        addCosmeticPlayerParticles(client);
    }

    public static void addCosmeticPlayerParticles(MinecraftClient client) {
        ClientWorld world = client.world;
        if (!client.isPaused() && world != null) {
            PlayerEntity shiny = world.getPlayerByUuid(GildedGloryClient.SHINY_UUID);
            Random random = Random.create();

            double offsetX = random.nextGaussian() * 0.35;
            double offsetY = random.nextGaussian() * 0.4;
            double offsetZ = random.nextGaussian() * 0.35;

            if (Math.random() < 0.175 && shiny != null) {
                GildedGloryUtil.addPersonalParticles(shiny, ModParticles.SPARKLE, shiny.getX() + offsetX, shiny.getBodyY(0.5) + offsetY, shiny.getZ() + offsetZ, 0, 0, 0);
            }
        }
    }

    public static void registerWorldRenderEvents() {
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            MinecraftClient client = MinecraftClient.getInstance();
            ClientPlayerEntity player = client.player;
            ClientWorld world = context.world();

            Camera camera = context.camera();
            RenderTickCounter counter = context.tickCounter();
            float tickDelta = counter.getTickDelta(false);

            //Chains Renderer
            for (Entity entity : world.getEntities()) {
                if (entity instanceof LivingEntity && (entity != player || !client.options.getPerspective().isFirstPerson())) {
                    ModComponents.CHAINED.maybeGet(entity).ifPresent(component -> {
                        if (component.getDuration() > 0) {
                            Entity counterpart = shiny.gildedglory.common.util.GildedGloryUtil.getEntityClient(component.getCounterpart(), world);

                            if (counterpart != null && component.isAttacker()) {
                                ChainRenderer.render(
                                        entity.getLerpedPos(tickDelta).add(0, entity.getHeight() / 2, 0),
                                        counterpart.getLerpedPos(tickDelta).add(0, counterpart.getHeight() / 2, 0),
                                        camera.getPos(),
                                        context.matrixStack(),
                                        context.consumers()
                                );
                            }
                        }
                    });
                }
            }

            //Slashed Areas
            SlashedAreaManager.renderTick(context);
        });
    }

    public static void registerHudRenderEvents() {
        MinecraftClient client = MinecraftClient.getInstance();
        HudRenderCallback.EVENT.register((context, renderTickCounter) -> {
            //Render custom crosshair elements
            if (client.player != null && client.options.getPerspective().isFirstPerson()) {
                ClientPlayerEntity player = client.player;
                ItemStack stack = player.getActiveItem();

                RenderSystem.enableBlend();
                RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);

                //Auradeus Charge Bars
                if (stack.isOf(ModItems.AURADEUS)) {
                    int i = player.getItemUseTime();

                    context.drawTexture(
                            OverlayRenderer.CROSSHAIR,
                            (context.getScaledWindowWidth() - 31) / 2,
                            (context.getScaledWindowHeight() - 31) / 2,
                            0,
                            Math.min((int) Math.floor((double) (3 * i) / 15), 3) * 32,
                            31, 31
                    );
                }

                //ChargeableWeapon Charge Meter
                if (stack.getItem() instanceof ChargeableWeapon weapon && weapon.chargeWhileUsing()) {

                    if (weapon.canLoseCharge(stack) || weapon.chargeSetOnStoppedUsing()) {
                        float f = (float) player.getItemUseTime() / weapon.getMaxCharge();
                        int i = context.getScaledWindowWidth() / 2 - 8;
                        int j = context.getScaledWindowHeight() / 2 - 7 + 16;
                        int k = (int) (15 * f);

                        context.drawTexture(OverlayRenderer.CROSSHAIR, i, j, 32, 0, 15, 4);
                        context.drawTexture(OverlayRenderer.CROSSHAIR, i, j, 32, 16, k, 4);
                    }
                }

                RenderSystem.defaultBlendFunc();
                RenderSystem.disableBlend();
            }

            //Render Chained Overlay
            if (chainedTick > 56) chainedTick = 0;

            ClientPlayerEntity player = client.player;
            if (player != null && ModComponents.CHAINED.get(player).getDuration() > 0) {
                int i = Math.floorDiv(chainedTick, 8) * 256;
                OverlayRenderer.renderChainedOverlay(context, i);
            }
            chainedTick++;
        });
    }
}
