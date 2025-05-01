package shiny.gildedglory.common.item;

import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

///An interface which can be implemented by other Item classes, which allows them to register custom attack sounds, particles, and hand poses.
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

    default boolean isTwoHanded(ItemStack stack) {
        return false;
    }

    default boolean overrideHandPoses(LivingEntity holder, ItemStack stack) {
        return false;
    }

    default BipedEntityModel.ArmPose getMainHandPose(ItemStack stack) {
        if (isTwoHanded(stack)) {
            return BipedEntityModel.ArmPose.BLOCK;
        }
        return BipedEntityModel.ArmPose.ITEM;
    }

    default BipedEntityModel.ArmPose getOffHandPose(ItemStack stack) {
        if (isTwoHanded(stack)) {
            return BipedEntityModel.ArmPose.CROSSBOW_CHARGE;
        }
        return BipedEntityModel.ArmPose.EMPTY;
    }
}
