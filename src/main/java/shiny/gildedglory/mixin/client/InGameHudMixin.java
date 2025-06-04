package shiny.gildedglory.mixin.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shiny.gildedglory.client.render.custom.OverlayRenderer;
import shiny.gildedglory.common.item.custom.ChargeableWeapon;
import shiny.gildedglory.common.registry.component.ModComponents;
import shiny.gildedglory.common.registry.item.ModItems;

@Mixin(value = InGameHud.class, priority = 499)
public abstract class InGameHudMixin {

    @Shadow private int scaledHeight;
    @Shadow private int scaledWidth;
    @Shadow @Final private MinecraftClient client;
    @Shadow protected abstract boolean shouldRenderSpectatorCrosshair(HitResult hitResult);

    @Unique private int tickCounter = 0;
    @Unique private boolean overrideAttackIndicator = false;

    @Inject(method = "renderCrosshair", at = @At(value = "HEAD"))
    private void gildedglory$renderCrosshairHud(DrawContext context, CallbackInfo ci) {
        GameOptions gameOptions = this.client.options;

        if (gameOptions.getPerspective().isFirstPerson()) {
            if (this.client.interactionManager.getCurrentGameMode() != GameMode.SPECTATOR || this.shouldRenderSpectatorCrosshair(this.client.crosshairTarget)) {
                boolean bl = !gameOptions.debugEnabled && !gameOptions.hudHidden;

                if (this.client.player != null) {
                    ClientPlayerEntity player = this.client.player;

                    RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
                    if (player.isUsingItem() && player.getActiveItem().isOf(ModItems.AURADEUS)) {
                        this.overrideAttackIndicator = true;

                        int i = player.getItemUseTime();
                        context.drawTexture(OverlayRenderer.CROSSHAIR, (this.scaledWidth - 31) / 2, (this.scaledHeight - 31) / 2, 0, Math.min((int) Math.floor((double) (3 * i) / 15), 3) * 32, 31, 31);
                    }

                    if (player.getActiveItem().getItem() instanceof ChargeableWeapon weapon) {
                        this.overrideAttackIndicator = true;

                        float f = (float) player.getItemUseTime() / weapon.getMaxCharge();
                        int i = this.scaledHeight / 2 - 7 + 16;
                        int j = this.scaledWidth / 2 - 8;
                        int k = (int) (16 * f);

                        context.drawTexture(OverlayRenderer.CROSSHAIR, j, i, 32, 0, 16, 4);
                        context.drawTexture(OverlayRenderer.CROSSHAIR, j, i, 32, 16, k, 4);
                    }
                    RenderSystem.defaultBlendFunc();
                }
            }
        }
    }

    //This somehow only hides the crosshair and not the attack indicator :/ Fix later
//    @WrapWithCondition(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V"))
//    private boolean gildedglory$overrideAttackIndicator(DrawContext instance, Identifier texture, int x, int y, int u, int v, int width, int height) {
//        boolean bl = this.overrideAttackIndicator;
//        this.overrideAttackIndicator = false;
//
//        return !bl;
//    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getFrozenTicks()I"))
    private void gildedglory$applyChainedOverlay(DrawContext context, float tickDelta, CallbackInfo ci) {
        if (tickCounter > 56) tickCounter = 0;

        ClientPlayerEntity player = client.player;
        if (player != null && ModComponents.CHAINED.get(player).getDuration() > 0) {
            int i = Math.floorDiv(tickCounter, 8) * 256;
            OverlayRenderer.renderChainedOverlay(context, i);
        }
        tickCounter++;
    }

    //Unused since keybind change
//    @Inject(method = "render", at = @At(value = "TAIL"))
//    private void gildedglory$applyIraedeusOverlay(DrawContext context, float tickDelta, CallbackInfo ci) {
//        ClientPlayerEntity player = client.player;
//        if (player != null) {
//            IraedeusComponent component = ModComponents.IRAEDEUS.get(player);
//            if (component.slot == player.getInventory().selectedSlot) {
//                OverlayRenderer.renderIraedeusOverlay(context, client.options.attackKey.isPressed() || client.options.useKey.isPressed());
//            }
//        }
//    }
}
