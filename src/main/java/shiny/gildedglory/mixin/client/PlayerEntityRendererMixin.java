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
import shiny.gildedglory.client.pose.ArmPose;
import shiny.gildedglory.common.item.custom.CustomEffectsWeapon;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin {

    @Inject(method = "getArmPose", at = @At(value = "RETURN"), cancellable = true)
    private static void gildedglory$customVanillaArmPose(AbstractClientPlayerEntity player, Hand hand, CallbackInfoReturnable<BipedEntityModel.ArmPose> cir) {
        ItemStack stack = player.getMainHandStack().getItem() instanceof CustomEffectsWeapon ? player.getMainHandStack() : player.getOffHandStack();

        if (stack.getItem() instanceof CustomEffectsWeapon weapon) {
            ArmPose mainPose = weapon.getMainHandPose(player, stack);
            ArmPose otherPose = weapon.getOffHandPose(player, stack);

            if (hand == Hand.MAIN_HAND && mainPose != null && mainPose.value() == ArmPose.Value.VANILLA) {
                cir.setReturnValue((BipedEntityModel.ArmPose) mainPose);
            }
            if (hand == Hand.OFF_HAND && otherPose != null && otherPose.value() == ArmPose.Value.VANILLA) {
                cir.setReturnValue((BipedEntityModel.ArmPose) otherPose);
            }
        }
    }
}
