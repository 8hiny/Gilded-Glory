package shiny.gildedglory.mixin.client.use_action;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shiny.gildedglory.client.use_action.BaseUseAction;
import shiny.gildedglory.client.use_action.CustomUseAction;
import shiny.gildedglory.client.use_action.UseActionContext;
import shiny.gildedglory.common.item.custom.CustomEffectsWeapon;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {

    @Shadow @Final private MinecraftClient client;
    @Shadow @Final private ItemRenderer itemRenderer;
    @Unique private ItemStack renderedStack;

    @WrapOperation(method = "renderFirstPersonItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getUseAction()Lnet/minecraft/util/UseAction;"))
    private UseAction gildedglory$applyCustomUseAction(ItemStack stack, Operation<UseAction> original) {
        if (stack.getItem() instanceof CustomEffectsWeapon weapon) {
            BaseUseAction action = weapon.getCustomUseAction(this.client.player, stack);
            if (action.value() != BaseUseAction.Value.EMPTY) {
                //Since I have no idea how to modify a switch statement (or if that is even possible),
                //this returns a useAction of NONE, so that I can then add custom transformations without
                //worrying about additional stuff
                return action.value() == BaseUseAction.Value.VANILLA ? (UseAction) action : UseAction.NONE;
            }
        }
        return original.call(stack);
    }

    @Inject(method = "renderFirstPersonItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;applyEquipOffset(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/Arm;F)V", ordinal = 2))
    private void gildedglory$customItemTransformationsBefore(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack stack, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (stack.getItem() instanceof CustomEffectsWeapon weapon) {
            BaseUseAction baseAction = weapon.getCustomUseAction(player, stack);
            if (baseAction instanceof CustomUseAction action && action.applyBefore()) {
                this.renderedStack = action.applyFirstPersonTransforms(new UseActionContext(
                        (HeldItemRenderer) (Object) this,
                        player,
                        tickDelta,
                        pitch,
                        light,
                        hand,
                        swingProgress,
                        player.getMainArm() == Arm.LEFT,
                        stack,
                        matrices,
                        vertexConsumers
                ));
            }
        }
    }

    @Inject(method = "renderFirstPersonItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;applyEquipOffset(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/Arm;F)V", ordinal = 2, shift = At.Shift.AFTER))
    private void gildedglory$customItemTransformationsAfter(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack stack, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (stack.getItem() instanceof CustomEffectsWeapon weapon) {
            BaseUseAction baseAction = weapon.getCustomUseAction(player, stack);
            if (baseAction instanceof CustomUseAction action && !action.applyBefore()) {
                this.renderedStack = action.applyFirstPersonTransforms(new UseActionContext(
                        (HeldItemRenderer) (Object) this,
                        player,
                        tickDelta,
                        pitch,
                        light,
                        hand,
                        swingProgress,
                        player.getMainArm() == Arm.LEFT,
                        stack,
                        matrices,
                        vertexConsumers
                ));
            }
        }
    }

    @WrapOperation(method = "renderFirstPersonItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", ordinal = 1))
    private void gildedglory$applyUseActionContext(HeldItemRenderer renderer, LivingEntity entity, ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Operation<Void> original) {
        if (this.renderedStack != null) {
            stack = renderedStack;
            this.renderedStack = null;
        }
        if (!stack.isEmpty()) original.call(renderer, entity, stack, renderMode, leftHanded, matrices, vertexConsumers, light);
    }
}
