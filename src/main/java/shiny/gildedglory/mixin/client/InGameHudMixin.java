package shiny.gildedglory.mixin.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shiny.gildedglory.common.item.custom.ChargeableWeapon;
import shiny.gildedglory.common.registry.item.ModItems;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    //TODO Potentially redo crosshair element textures

    @Shadow @Final private MinecraftClient client;

    @Shadow @Final private static Identifier CROSSHAIR_TEXTURE;
    @Unique private boolean hideAttackIndicator = false;

    @Inject(method = "renderCrosshair", at = @At(value = "HEAD"))
    private void gildedglory$setUpOptions(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (this.client.player != null) {
            ClientPlayerEntity player = this.client.player;
            ItemStack stack = player.getActiveItem();

            if (stack.isOf(ModItems.AURADEUS)) this.hideAttackIndicator = true;
            if (stack.getItem() instanceof ChargeableWeapon) this.hideAttackIndicator = ChargeableWeapon.getChargePercentage(stack) == 0;
        }
    }

    @WrapWithCondition(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V"))
    private boolean gildedglory$hideAttackIndicator(DrawContext context, Identifier texture, int x, int y, int width, int height) {
        return texture == CROSSHAIR_TEXTURE || !this.hideAttackIndicator;
    }

    @WrapWithCondition(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIIIIIII)V"))
    private boolean gildedglory$hideAttackIndicatorProgress(DrawContext context, Identifier texture, int i, int j, int k, int l, int x, int y, int width, int height) {
        return !this.hideAttackIndicator;
    }

    @Inject(method = "renderCrosshair", at = @At(value = "TAIL"))
    private void gildedglory$resetOptions(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        this.hideAttackIndicator = false;
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
