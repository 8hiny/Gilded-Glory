package shiny.gildedglory.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import shiny.gildedglory.common.entity.IraedeusEntity;
import shiny.gildedglory.common.item.ChargeableWeapon;
import shiny.gildedglory.common.item.FoolsArmorItem;
import shiny.gildedglory.common.registry.component.ModComponents;
import shiny.gildedglory.common.registry.item.ModToolMaterials;

@Mixin(value = LivingEntity.class, priority = 499)
public abstract class LivingEntityMixin extends Entity {

    @Shadow public abstract Iterable<ItemStack> getArmorItems();
    @Shadow public abstract float getYaw(float tickDelta);

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @WrapWithCondition(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;onDeath(Lnet/minecraft/entity/damage/DamageSource;)V"))
    private boolean gildedglory$preventDeath(LivingEntity entity, DamageSource damageSource) {
        if (ModComponents.FOOLS_STATUE.get(this).getDuration() > 0 && damageSource.getAttacker() instanceof LivingEntity attacker) {
            Item weapon = attacker.getMainHandStack().getItem();

            if (weapon instanceof ToolItem toolItem && toolItem.getMaterial() == ModToolMaterials.TWISTEEL) {
                ModComponents.FOOLS_STATUE.get(this).disable();
                for (ItemStack item : this.getArmorItems()) {
                    if (item.getItem() instanceof FoolsArmorItem) item.decrement(1);
                }
                this.getWorld().playSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_ITEM_BREAK, this.getSoundCategory(), 1.0f, 1.0f, true);
                return true;
            }
        }

        if (FoolsArmorItem.hasFullSet(entity) && !damageSource.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            ModComponents.FOOLS_STATUE.get(this).setDuration(6000);
            entity.setHealth(entity.getMaxHealth());
            return false;
        }
        return true;
    }

    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;onDeath(Lnet/minecraft/entity/damage/DamageSource;)V"))
    private void gildedglory$onDeath(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (entity instanceof PlayerEntity player) {
            //Return thrown iraedeus's to their owners
            if (ModComponents.IRAEDEUS.get(player).slot != -1) {
                for (IraedeusEntity iraedeus : player.getWorld().getEntitiesByClass(IraedeusEntity.class,
                        player.getBoundingBox().expand(50.0),
                        iraedeusEntity -> iraedeusEntity.getOwner() != null && iraedeusEntity.getOwner().getUuid() == player.getUuid())
                ) {
                    iraedeus.insertStack(player);
                    iraedeus.discard();
                }
            }
            //Break chains (necessary because of doImmediateRespawn; a ticking method can't catch this effectively enough)
            if (ModComponents.CHAINED.get(player).getDuration() > 0) {
                ModComponents.CHAINED.get(player).unChain();
            }
        }
    }

    @Inject(method = "canTarget(Lnet/minecraft/entity/LivingEntity;)Z", at = @At(value = "RETURN"), cancellable = true)
    private void gildedglory$preventFoolsArmorTarget(LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(cir.getReturnValue() && ModComponents.FOOLS_STATUE.get(target).getDuration() == 0);
    }

    @Inject(method = "damage", at = @At(value = "RETURN"))
    private void gildedglory$stopCharging(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            LivingEntity entity = (LivingEntity) (Object) this;
            ItemStack stack = entity.getOffHandStack();

            if (!(stack.getItem() instanceof ChargeableWeapon weapon && weapon.offHandUsable())) {
                stack = entity.getMainHandStack();
            }

            if (stack.getItem() instanceof ChargeableWeapon weapon && (entity.isUsingItem() || weapon.canLoseCharge(stack))) {
                ChargeableWeapon.setCharge(stack, 0);
                if (entity instanceof PlayerEntity player && !player.getItemCooldownManager().isCoolingDown(stack.getItem())) {
                    player.getItemCooldownManager().set(stack.getItem(), 40);
                }
                entity.clearActiveItem();
            }
        }
    }
}
