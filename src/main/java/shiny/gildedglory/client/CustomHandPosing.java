package shiny.gildedglory.client;

import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import shiny.gildedglory.common.item.ChargeableWeapon;
import shiny.gildedglory.common.registry.item.ModItems;

public class CustomHandPosing {

    /**
     * Overrides the angles for the limbs of any living entity's model. Called before limbs are animated.
     */
    public static <T extends LivingEntity> void setAngles(T entity, BipedEntityModel<T> model, float animationProgress) {
        ItemStack main = entity.getMainHandStack();
        ItemStack other = entity.getOffHandStack();
        ItemStack using = entity.getActiveItem();

        //Auradeus charging pose
        if (using.isOf(ModItems.AURADEUS) && entity.getItemUseTime() > 1) {
            model.leftArm.yaw = -0.55f;
            model.leftArm.pitch = entity.isSneaking() ? -1.1f : -0.7f;

            model.rightArm.yaw = -0.8f;
            model.rightArm.pitch = entity.isSneaking() ? -1.9f : -1.5f;
            model.rightArm.roll = 1.2f;
        }
        //Backwards offhand pose
        if ((other.isOf(ModItems.AURADEUS) || other.isOf(ModItems.KATANA)) && main.isEmpty()) {
            model.leftArm.yaw = 0.3f;
            model.leftArm.pitch = entity.isSneaking() ? -1.0f : -0.6f;
            model.leftArm.roll = -0.3f;

            model.rightArm.yaw = -1f;
            model.rightArm.pitch = entity.isSneaking() ? -1.9f : -1.5f;
            model.rightArm.roll = 0.3f;

        }

        //Swordspear charging & firing pose
        if (using.isOf(ModItems.SWORDSPEAR) && entity.getItemUseTime() > 0) {
            model.leftArm.pitch = Math.min(model.head.pitch + 80.0f, 80.77f);
            model.rightArm.pitch = Math.min(model.head.pitch + 80.0f, 80.77f);
        }
    }

    /**
     * Same as setAngles, but called after limbs are animated.
     * @see CustomHandPosing#setAngles(LivingEntity, BipedEntityModel, float)
     */
    public static <T extends LivingEntity> void setFinalAngles(T entity, BipedEntityModel<T> model, float animationProgress) {
        ItemStack main = entity.getMainHandStack();
        ItemStack other = entity.getOffHandStack();
        ItemStack using = entity.getActiveItem();

        //Swordspear firing pose
        if (main.isOf(ModItems.SWORDSPEAR) && ChargeableWeapon.getCharge(main) > 0) {
            model.leftArm.yaw = 0.4f + model.head.yaw;
            model.leftArm.pitch = (float) (-Math.PI / 2) + model.head.pitch;
            model.leftArm.roll = 0.0f;

            model.rightArm.yaw = -0.4f + model.head.yaw;
            model.rightArm.pitch = (float) (-Math.PI / 2) + model.head.pitch;
            model.rightArm.roll = 0.0f;
        }
    }
}
