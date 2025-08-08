package shiny.gildedglory.client.pose;

import net.minecraft.registry.Registry;
import shiny.gildedglory.GildedGlory;
import shiny.gildedglory.common.registry.ModRegistries;

public class CustomArmPoses {

    public static CustomArmPose TWO_HANDED_HOLDING = register("two_handed_holding", new CustomArmPose(
            (leftArm, ctx) -> {
                leftArm.yaw = 0.8f;
                leftArm.pitch = -0.97079635f;
            },
            (rightArm, ctx) -> {
                rightArm.yaw = -0.4f;
                rightArm.pitch = -0.97079635f;
            },
            false
    ));
    public static CustomArmPose SIDEWAYS_CHARGING = register("sideways_charging", new CustomArmPose(
            (leftArm, ctx) -> {
                leftArm.yaw = -0.55f;
                leftArm.pitch = ctx.entity().isSneaking() ? -1.1f : -0.7f;
            },
            (rightArm, ctx) -> {
                rightArm.yaw = -0.8f;
                rightArm.pitch = ctx.entity().isSneaking() ? -1.9f : -1.5f;
                rightArm.roll = 1.2f;
            },
            false
    ));
    public static CustomArmPose FORWARDS_CHARGING = register("forwards_charging", new CustomArmPose(
            (leftArm, ctx) -> {
                leftArm.pitch = Math.min(ctx.headPitch() + 80.0f, 80.77f);
            },
            (rightArm, ctx) -> {
                rightArm.pitch = Math.min(ctx.headPitch() + 80.0f, 80.77f);
            },
            false
    ));
    public static CustomArmPose BACKWARDS_HOLDING = register("backwards_holding", new CustomArmPose(
            (leftArm, ctx) -> {
                leftArm.yaw = 0.3f;
                leftArm.pitch = ctx.entity().isSneaking() ? -1.0f : -0.6f;
                leftArm.roll = -0.3f;
            },
            (rightArm, ctx) -> {
                rightArm.yaw = -1f;
                rightArm.pitch = ctx.entity().isSneaking() ? -1.9f : -1.5f;
                rightArm.roll = 0.3f;
            },
            false
    ));

    //TODO Fix the below pose aiming the arms outwards when facing up or down (looks especially bad with the beam cause it's so long)
    public static CustomArmPose FORWARDS_AIMING = register("forwards_aiming", new CustomArmPose(
            (leftArm, ctx) -> {
                leftArm.yaw = 0.4f - ctx.headPitch();
                leftArm.pitch = (float) (-Math.PI / 2) + ctx.headPitch();
                leftArm.roll = 0.0f;
            },
            (rightArm, ctx) -> {
                rightArm.yaw = -0.4f - ctx.headPitch();
                rightArm.pitch = (float) (-Math.PI / 2) + ctx.headPitch();
                rightArm.roll = 0.0f;
            },
            true
    ));

    public static CustomArmPose register(String name, CustomArmPose pose) {
        return Registry.register(ModRegistries.CUSTOM_ARM_POSE, GildedGlory.id(name), pose);
    }

    public static void registerCustomArmPoses() {
    }
}
