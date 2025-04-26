package shiny.gildedglory.mixin.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Arm;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shiny.gildedglory.common.item.ChargeableWeapon;
import shiny.gildedglory.common.item.CustomEffectsWeapon;
import shiny.gildedglory.common.registry.item.ModItems;

@Mixin(BipedEntityModel.class)
public abstract class BipedEntityModelMixin<T extends LivingEntity> extends AnimalModel<T> implements ModelWithArms, ModelWithHead {

    @Shadow @Final public ModelPart rightArm;
    @Shadow @Final public ModelPart leftArm;
    @Shadow @Final public ModelPart head;

    @Shadow protected abstract Arm getPreferredArm(T entity);
    @Shadow protected abstract ModelPart getArm(Arm arm);

    @WrapWithCondition(method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/BipedEntityModel;animateArms(Lnet/minecraft/entity/LivingEntity;F)V"))
    private boolean gildedglory$customArmPose(BipedEntityModel model, T entity, float animationProgress) {

        //Auradeus charging pose
        if (entity.getActiveItem().isOf(ModItems.AURADEUS) && entity.getItemUseTime() > 1) {
            this.leftArm.yaw = -0.55f;
            this.leftArm.pitch = entity.isSneaking() ? -1.1f : -0.7f;

            this.rightArm.yaw = -0.8f;
            this.rightArm.pitch = entity.isSneaking() ? -1.9f : -1.5f;
            this.rightArm.roll = 1.2f;
        }
        //Auradeus offhand pose
        if (entity.getOffHandStack().isOf(ModItems.AURADEUS) && entity.getMainHandStack().isEmpty()) {
            this.leftArm.yaw = 0.3f;
            this.leftArm.pitch = entity.isSneaking() ? -1.0f : -0.6f;
            this.leftArm.roll = -0.3f;

            this.rightArm.yaw = -1f;
            this.rightArm.pitch = entity.isSneaking() ? -1.9f : -1.5f;
            this.rightArm.roll = 0.3f;

        }

        //Swordspear charging & firing pose
        if (entity.getActiveItem().isOf(ModItems.SWORDSPEAR) && entity.getItemUseTime() > 0) {
            this.leftArm.pitch = Math.min(this.head.pitch + 80.0f, 80.77f);
            this.rightArm.pitch = Math.min(this.head.pitch + 80.0f, 80.77f);
        }
        return true;
    }

    @Inject(method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V", at = @At(value = "TAIL"))
    private void gildedglory$customStaticArmPose(T entity, float f, float g, float h, float i, float j, CallbackInfo ci) {
        if (entity.getMainHandStack().isOf(ModItems.SWORDSPEAR) && ChargeableWeapon.getCharge(entity.getMainHandStack()) > 0) {
            this.leftArm.yaw = 0.4f + this.head.yaw;
            this.leftArm.pitch = (float) (-Math.PI / 2) + this.head.pitch;
            this.leftArm.roll = 0.0f;

            this.rightArm.yaw = -0.4f + this.head.yaw;
            this.rightArm.pitch = (float) (-Math.PI / 2) + this.head.pitch;
            this.rightArm.roll = 0.0f;
        }
    }

    @Inject(method = "animateArms", at = @At(value = "TAIL"))
    private void gildedglory$twoHandedAttack(T entity, float animationProgress, CallbackInfo ci) {
        if (entity.getMainHandStack().getItem() instanceof CustomEffectsWeapon weapon && weapon.isTwoHanded()) {
            ModelPart mainArm = this.getArm(this.getPreferredArm(entity));
            ModelPart otherArm = this.getArm(this.getPreferredArm(entity).getOpposite());

            otherArm.pitch = mainArm.pitch;
            otherArm.yaw = mainArm.yaw / 2;
        }
    }
}
