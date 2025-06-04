package shiny.gildedglory.mixin.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shiny.gildedglory.client.pose.ArmPose;
import shiny.gildedglory.client.pose.CustomArmPose;
import shiny.gildedglory.client.pose.CustomArmPosing;
import shiny.gildedglory.common.item.custom.CustomEffectsWeapon;

@Mixin(BipedEntityModel.class)
public abstract class BipedEntityModelMixin<T extends LivingEntity> extends AnimalModel<T> implements ModelWithArms, ModelWithHead {

    @Shadow @Final public ModelPart head;
    @Shadow @Final public ModelPart body;
    @Shadow @Final public ModelPart leftArm;
    @Shadow @Final public ModelPart rightArm;
    @Shadow protected abstract Arm getPreferredArm(T entity);
    @Shadow protected abstract ModelPart getArm(Arm arm);
    @Unique private boolean preventLimbSwing = false;

    @Inject(method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/BipedEntityModel;animateArms(Lnet/minecraft/entity/LivingEntity;F)V"))
    private void gildedglory$applyArmTransformations(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch, CallbackInfo ci) {
        ItemStack stack = CustomEffectsWeapon.getWeapon(entity);
        if (stack != null) {
            ArmPose mainPose = ((CustomEffectsWeapon) stack.getItem()).getMainHandPose(entity, stack);
            ArmPose otherPose = ((CustomEffectsWeapon) stack.getItem()).getMainHandPose(entity, stack);
            CustomArmPosing.positionLeftArm(entity, otherPose, this.leftArm, this.head.pitch, this.head.yaw, this.body.pitch, this.body.yaw, entity.getMainArm() == Arm.LEFT);
            CustomArmPosing.positionRightArm(entity, mainPose, this.rightArm, this.head.pitch, this.head.yaw, this.body.pitch, this.body.yaw, entity.getMainArm() == Arm.LEFT);

            this.preventLimbSwing = (mainPose instanceof CustomArmPose && ((CustomArmPose) mainPose).preventLimbSwing())
                    || (otherPose instanceof CustomArmPose && ((CustomArmPose) otherPose).preventLimbSwing());
        }
    }

    @WrapOperation(method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/BipedEntityModel;animateArms(Lnet/minecraft/entity/LivingEntity;F)V"))
    private void gildedglory$preventAttackAnimation(BipedEntityModel<T> model, T entity, float animationProgress, Operation<Void> original) {
        ItemStack stack = entity.getOffHandStack();
        if (entity.getMainHandStack().isEmpty() && stack.getItem() instanceof CustomEffectsWeapon weapon) {
            ArmPose pose = weapon.getCustomPose(entity, stack);
            if (pose != null && pose.twoHanded()) this.handSwingProgress = 0.0f;
        }
        original.call(model, entity, animationProgress);
    }

    @WrapWithCondition(method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/CrossbowPosing;swingArm(Lnet/minecraft/client/model/ModelPart;FF)V"))
    private boolean gildedglory$preventArmSway(ModelPart arm, float animationProgress, float sigma) {
        boolean bl = this.preventLimbSwing;
        this.preventLimbSwing = false;
        return !bl;
    }

    @Inject(method = "animateArms", at = @At(value = "TAIL"))
    private void gildedglory$twoHandedAttack(T entity, float animationProgress, CallbackInfo ci) {
        ItemStack stack = entity.getMainHandStack();
        if (stack.getItem() instanceof CustomEffectsWeapon weapon && this.handSwingProgress > 0.0f) {

            ArmPose pose = weapon.getCustomPose(entity, stack);
            if (pose != null && pose.twoHanded()) {
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
}
