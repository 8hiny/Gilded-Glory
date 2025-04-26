package shiny.gildedglory.common.item;

import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.sound.SoundEvent;

public interface CustomEffectsWeapon {

    default SoundEvent getDefaultAttackSound(ItemStack stack) {
        return null;
    }

    default SoundEvent getCritAttackSound(ItemStack stack) {
        return null;
    }

    default SoundEvent getKnockbackAttackSound(ItemStack stack) {
        return null;
    }

    default SoundEvent getNoDamageAttackSound(ItemStack stack) {
        return null;
    }

    default SoundEvent getStrongAttackSound(ItemStack stack) {
        return null;
    }

    default SoundEvent getSweepAttackSound(ItemStack stack) {
        return null;
    }

    default SoundEvent getWeakAttackSound(ItemStack stack) {
        return null;
    }

    default DefaultParticleType getAttackParticle() {
        return null;
    }

    default DefaultParticleType getSweepAttackParticle() {
        return null;
    }

    default DefaultParticleType getCritAttackParticle() {
        return null;
    }

    default BipedEntityModel.ArmPose getMainHandPose() {
        return BipedEntityModel.ArmPose.ITEM;
    }

    default BipedEntityModel.ArmPose getOffHandPose() {
        return BipedEntityModel.ArmPose.EMPTY;
    }

    default boolean alwaysPlayDefaultAttackSound() {
        return false;
    }

    default boolean isTwoHanded() {
        return getMainHandPose() != null && getMainHandPose().isTwoHanded();
    }

    default boolean overrideHandPoses(LivingEntity holder, ItemStack stack) {
        return false;
    }
}
