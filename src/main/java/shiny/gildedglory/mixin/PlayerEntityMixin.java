package shiny.gildedglory.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import shiny.gildedglory.common.item.*;
import shiny.gildedglory.common.registry.component.ModComponents;
import shiny.gildedglory.common.util.GildedGloryUtil;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    @Shadow public abstract float getAttackCooldownProgress(float baseTime);
    @Shadow public abstract boolean damage(DamageSource source, float amount);

    @Unique private float attackCooldownProgress = 0.0f;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "attack", at = @At(value = "HEAD"))
    private void gildedglory$getAttackCooldownBeforeAttack(Entity target, CallbackInfo ci) {
        this.attackCooldownProgress = this.getAttackCooldownProgress(0.0f);

    }

    @Inject(method = "attack", at = @At(value = "HEAD"))
    private void gildedglory$defaultAttackSound(Entity target, CallbackInfo ci) {
        ItemStack stack = this.getMainHandStack();
        if (stack.getItem() instanceof CustomEffectsWeapon weapon && weapon.getDefaultAttackSound(stack) != null && (weapon.alwaysPlayDefaultAttackSound(stack) || this.attackCooldownProgress > 0.8f)) {
            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), weapon.getDefaultAttackSound(stack), this.getSoundCategory(), 1.0f, GildedGloryUtil.random(0.9f, 1.1f));
        }
    }

    @ModifyArgs(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"))
    private void gildedglory$customAttackParticles(Args args) {
        double d = -MathHelper.sin(this.getYaw() * (float) (Math.PI / 180.0));
        double e = MathHelper.cos(this.getYaw() * (float) (Math.PI / 180.0));

        ItemStack stack = this.getMainHandStack();
        if (!this.getWorld().isClient() && this.getMainHandStack().getItem() instanceof CustomEffectsWeapon weapon) {
            if (weapon.getCritAttackParticle(stack) != null && args.get(4) == SoundEvents.ENTITY_PLAYER_ATTACK_CRIT) {
                ((ServerWorld) this.getWorld()).spawnParticles(weapon.getCritAttackParticle(stack), this.getX() + d, this.getBodyY(0.5), this.getZ() + e, 0, d, 0.0, e, 0.0);
            }
            else if (weapon.getAttackParticle(stack) != null && args.get(4) != SoundEvents.ENTITY_PLAYER_ATTACK_WEAK && args.get(4) != SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE) {
                ((ServerWorld) this.getWorld()).spawnParticles(weapon.getAttackParticle(stack), this.getX() + d, this.getBodyY(0.5), this.getZ() + e, 0, d, 0.0, e, 0.0);
            }
        }
    }

    @ModifyArgs(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"))
    private void gildedglory$customAttackSounds(Args args) {
        ItemStack stack = this.getMainHandStack();
        if (stack.getItem() instanceof CustomEffectsWeapon weapon) {
            float pitch = GildedGloryUtil.random(0.9f, 1.1f);
            SoundEvent sound = args.get(4);

            args.set(7, pitch);
            if (sound == SoundEvents.ENTITY_PLAYER_ATTACK_CRIT && weapon.getCritAttackSound(stack) != null)
                args.set(4, weapon.getCritAttackSound(stack));
            else if (sound == SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK && weapon.getKnockbackAttackSound(stack) != null)
                args.set(4, weapon.getKnockbackAttackSound(stack));
            else if (sound == SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE && weapon.getNoDamageAttackSound(stack) != null)
                args.set(4, weapon.getNoDamageAttackSound(stack));
            else if (sound == SoundEvents.ENTITY_PLAYER_ATTACK_STRONG && weapon.getStrongAttackSound(stack) != null)
                args.set(4, weapon.getStrongAttackSound(stack));
            else if (sound == SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP && weapon.getSweepAttackSound(stack) != null)
                args.set(4, weapon.getSweepAttackSound(stack));
            else if (sound == SoundEvents.ENTITY_PLAYER_ATTACK_WEAK && weapon.getWeakAttackSound(stack) != null)
                args.set(4, weapon.getWeakAttackSound(stack));
        }
    }

    @ModifyArgs(method = "spawnSweepAttackParticles", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;spawnParticles(Lnet/minecraft/particle/ParticleEffect;DDDIDDDD)I"))
    private void gildedglory$customSweepParticle(Args args) {
        ItemStack stack = this.getMainHandStack();
        if (this.getMainHandStack().getItem() instanceof CustomEffectsWeapon weapon && weapon.getSweepAttackParticle(stack) != null) {
            args.set(0, weapon.getSweepAttackParticle(stack));
        }
    }

    @WrapOperation(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"))
    private boolean gildedglory$handleCustomAttack(Entity target, DamageSource source, float amount, Operation<Boolean> original) {
        ItemStack stack = this.getMainHandStack();

        if (!target.isInvulnerableTo(source) && this.attackCooldownProgress > 0.8f && stack.getItem() instanceof CustomAttackWeapon weapon) {
            CustomAttackWeapon.CustomAttackData attack = weapon.onAttack(stack, this, target, source, amount);

            if (attack.successful()) {
                return original.call(attack.target(), attack.source(), attack.amount());
            }
            else {
                return false;
            }
        }
        return original.call(target, source, amount);
    }

    @ModifyReturnValue(method = "isImmobile", at = @At(value = "RETURN"))
    private boolean gildedglory$isImmobile(boolean original) {
        return original || ModComponents.FOOLS_STATUE.get(this).getDuration() > 0;
    }

    @Inject(method = "handleFallDamage", at = @At(value = "HEAD"), cancellable = true)
    private void gildedglory$preventFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;

        if (ModComponents.CHAINED.get(player).getDuration() > 0) {
            cir.setReturnValue(false);
        }
    }
}