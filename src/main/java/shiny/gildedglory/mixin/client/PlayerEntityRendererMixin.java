package shiny.gildedglory.mixin.client;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import shiny.gildedglory.common.item.CustomEffectsWeapon;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin {

    @Inject(method = "getArmPose", at = @At(value = "RETURN"), cancellable = true)
    private static void gildedglory$setCustomArmPose(AbstractClientPlayerEntity player, Hand hand, CallbackInfoReturnable<BipedEntityModel.ArmPose> cir) {
        ItemStack stack = player.getMainHandStack();
        if (stack.getItem() instanceof CustomEffectsWeapon weapon && !weapon.overrideHandPoses(player, stack)) {
            if (hand == Hand.MAIN_HAND && weapon.getMainHandPose(stack) != null) cir.setReturnValue(weapon.getMainHandPose(stack));
            else if (weapon.getOffHandPose(stack) != null) cir.setReturnValue(weapon.getOffHandPose(stack));
        }
    }
}
