package shiny.gildedglory.client.pose;

import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.LivingEntity;

public class CustomArmPosing {

    public static <T extends LivingEntity> void positionLeftArm(T entity, ArmPose pose, ModelPart leftArm, float headPitch, float headYaw, float bodyPitch, float bodyYaw, boolean leftHanded) {
        if (pose != null && pose.value() != ArmPose.Value.VANILLA) {
            if (pose instanceof CustomArmPose pose1) {
                pose1.transformLeft(leftArm, new CustomArmPose.Context(entity, headPitch, headYaw, bodyPitch, bodyYaw, leftHanded));
            }
        }
    }

    public static <T extends LivingEntity> void positionRightArm(T entity, ArmPose pose, ModelPart rightArm, float headPitch, float headYaw, float bodyPitch, float bodyYaw, boolean leftHanded) {
        if (pose != null && pose.value() != ArmPose.Value.VANILLA) {
            if (pose instanceof CustomArmPose pose1) {
                pose1.transformRight(rightArm, new CustomArmPose.Context(entity, headPitch, headYaw, bodyPitch, bodyYaw, leftHanded));
            }
        }
    }
}
