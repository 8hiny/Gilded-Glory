package shiny.gildedglory.common.item.custom;

import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import shiny.gildedglory.client.pose.ArmPose;

///An interface which can be implemented by other Item classes, which allows them to register custom attack sounds, particles, and arm poses.
public interface CustomEffectsWeapon {

    default SoundEvent getDefaultAttackSound(ItemStack stack) {
        return null;
    }

    default SoundEvent getCritAttackSound(ItemStack stack) {
        return SoundEvents.ENTITY_PLAYER_ATTACK_CRIT;
    }

    default SoundEvent getKnockbackAttackSound(ItemStack stack) {
        return SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK;
    }

    default SoundEvent getNoDamageAttackSound(ItemStack stack) {
        return SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE;
    }

    default SoundEvent getStrongAttackSound(ItemStack stack) {
        return SoundEvents.ENTITY_PLAYER_ATTACK_STRONG;
    }

    default SoundEvent getSweepAttackSound(ItemStack stack) {
        return SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP;
    }

    default SoundEvent getWeakAttackSound(ItemStack stack) {
        return SoundEvents.ENTITY_PLAYER_ATTACK_WEAK;
    }

    default boolean alwaysPlayDefaultAttackSound(ItemStack stack) {
        return false;
    }

    default DefaultParticleType getAttackParticle(ItemStack stack) {
        return null;
    }

    default DefaultParticleType getSweepAttackParticle(ItemStack stack) {
        return null;
    }

    default DefaultParticleType getCritAttackParticle(ItemStack stack) {
        return null;
    }

    /// Returns the pose for the holder's main hand when this item is held.
    default ArmPose getMainHandPose(LivingEntity holder, ItemStack stack) {
        return holder.getMainHandStack() == stack ? BipedEntityModel.ArmPose.ITEM : BipedEntityModel.ArmPose.EMPTY;
    }

    /// Returns the pose for the holder's offhand when this item is held.
    default ArmPose getOffHandPose(LivingEntity holder, ItemStack stack) {
        return holder.getOffHandStack() == stack ? BipedEntityModel.ArmPose.ITEM : BipedEntityModel.ArmPose.EMPTY;
    }

    /// Returns any custom pose which this item might apply to the holder. Prioritizes the main hand.
    default ArmPose getCustomPose(LivingEntity holder, ItemStack stack) {
        ArmPose mainPose = getMainHandPose(holder, stack);
        ArmPose otherPose = getOffHandPose(holder, stack);

        if (mainPose != null && isCustom(mainPose)) return mainPose;
        else if (otherPose != null && isCustom(otherPose)) return otherPose;
        return null;
    }

    static ArmPose getCustomPose(LivingEntity holder) {
        ItemStack stack = getWeapon(holder);
        if (stack != null) {
            return ((CustomEffectsWeapon) stack.getItem()).getCustomPose(holder, stack);
        }
        return null;
    }

    /// Returns any CustomEffectsWeapon which the holder might be holding. Prioritizes the main hand.
    static ItemStack getWeapon(LivingEntity holder) {
        if (holder.getMainHandStack().getItem() instanceof CustomEffectsWeapon) return holder.getMainHandStack();
        else if (holder.getOffHandStack().getItem() instanceof CustomEffectsWeapon) return holder.getOffHandStack();
        else return null;
    }

    static boolean isCustom(ArmPose pose) {
        return pose.value() != ArmPose.Value.VANILLA || (pose != BipedEntityModel.ArmPose.EMPTY && pose != BipedEntityModel.ArmPose.ITEM);
    }
}
