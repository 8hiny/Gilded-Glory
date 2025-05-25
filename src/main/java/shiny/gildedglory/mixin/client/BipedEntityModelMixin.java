package shiny.gildedglory.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shiny.gildedglory.client.CustomHandPosing;
import shiny.gildedglory.common.item.CustomEffectsWeapon;

@Mixin(BipedEntityModel.class)
public abstract class BipedEntityModelMixin<T extends LivingEntity> extends AnimalModel<T> implements ModelWithArms, ModelWithHead {

    @Shadow @Final public ModelPart head;
    @Shadow @Final public ModelPart body;
    @Shadow protected abstract Arm getPreferredArm(T entity);
    @Shadow protected abstract ModelPart getArm(Arm arm);

    @WrapOperation(method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/BipedEntityModel;animateArms(Lnet/minecraft/entity/LivingEntity;F)V"))
    private void gildedglory$customArmPose(BipedEntityModel<T> model, T entity, float animationProgress, Operation<Void> original) {
        CustomHandPosing.setAngles(entity, model, animationProgress);
        original.call(model, entity, animationProgress);
    }

    @Inject(method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V", at = @At(value = "TAIL"))
    private void gildedglory$customStaticArmPose(T entity, float f, float g, float h, float i, float j, CallbackInfo ci) {
        BipedEntityModel<T> model = (BipedEntityModel<T>) (Object) this;
        CustomHandPosing.setFinalAngles(entity, model, h);
    }

    @Inject(method = "animateArms", at = @At(value = "TAIL"))
    private void gildedglory$twoHandedAttack(T entity, float animationProgress, CallbackInfo ci) {
        ItemStack stack = entity.getMainHandStack();
        if (stack.getItem() instanceof CustomEffectsWeapon weapon && weapon.isTwoHanded(stack) && this.handSwingProgress > 0.0f) {
            ModelPart arm = this.getArm(this.getPreferredArm(entity).getOpposite());

            float f = 1.0f - this.handSwingProgress;
            f *= f;
            f *= f;
            f = 1.0f - f;
            float g = MathHelper.sin(f * (float) Math.PI);
            float h = MathHelper.sin(this.handSwingProgress * (float) Math.PI) * -(this.head.pitch - 0.7f) * 0.75f;
            arm.pitch -= g * 1.2f + h;
            arm.yaw = arm.yaw + this.body.yaw * 2.0f;
            arm.roll = arm.roll + MathHelper.sin(this.handSwingProgress * (float) Math.PI) * -0.4f;
        }
    }
}
