package shiny.gildedglory.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shiny.gildedglory.common.item.custom.CustomAttackWeapon;
import shiny.gildedglory.common.item.custom.CustomEffectsWeapon;
import shiny.gildedglory.common.util.GildedGloryUtil;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity {

    protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @WrapOperation(method = "tryAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"))
    private boolean gildedglory$handleCustomAttack(Entity target, DamageSource source, float amount, Operation<Boolean> original) {
        ItemStack stack = this.getMainHandStack();

        if (stack.getItem() instanceof CustomEffectsWeapon weapon) {
            if (weapon.getAttackParticle(stack) != null) {
                double d = -MathHelper.sin(this.getYaw() * (float) (Math.PI / 180.0));
                double e = MathHelper.cos(this.getYaw() * (float) (Math.PI / 180.0));
                ((ServerWorld) this.getWorld()).spawnParticles(weapon.getAttackParticle(stack), this.getX() + d, this.getBodyY(0.5), this.getZ() + e, 0, d, 0.0, e, 0.0);
            }

            if (weapon.getDefaultAttackSound(stack) != null) {
                this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), weapon.getDefaultAttackSound(stack), this.getSoundCategory(), 1.0f, GildedGloryUtil.random(0.9f, 1.1f));
            }
        }

        if (!target.isInvulnerableTo(source) && stack.getItem() instanceof CustomAttackWeapon weapon) {
            CustomAttackWeapon.AttackContext attack = weapon.onAttack(stack, this, target, source, amount, false, true);

            if (attack.successful()) {
                return original.call(attack.target(), attack.source(), attack.amount());
            }
            else {
                return false;
            }
        }
        return original.call(target, source, amount);
    }

    //Remove this if it seems to cause any sort of lag
    @Inject(method = "tick", at = @At(value = "TAIL"))
    private void gildedglory$mobInventoryTick(CallbackInfo ci) {
        if (this.getMainHandStack() != null) {
            this.getMainHandStack().inventoryTick(this.getWorld(), this, 98, true);
        }
    }
}
