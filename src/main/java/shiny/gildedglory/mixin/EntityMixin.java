package shiny.gildedglory.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import shiny.gildedglory.common.item.ChargeableWeapon;
import shiny.gildedglory.common.registry.component.ModComponents;
import shiny.gildedglory.common.registry.item.ModItems;
import shiny.gildedglory.common.util.GildedGloryUtil;

import java.util.UUID;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow public abstract World getWorld();

    //Overrides teleportation for living entities other than players; that is handled in ServerPlayerEntityMixin
    @Inject(method = "requestTeleport", at = @At(value = "HEAD"), cancellable = true)
    private void gildedglory$preventTeleportAndBreakChains(double destX, double destY, double destZ, CallbackInfo ci) {
        Entity thisEntity = (Entity) (Object) this;
        if (thisEntity instanceof LivingEntity livingEntity && ModComponents.CHAINED.get(livingEntity).getDuration() > 0 && !this.getWorld().isClient()) {
            ModComponents.CHAINED.get(livingEntity).unChain();
            ci.cancel();
        }
    }

    @WrapOperation(method = "updateVelocity", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;add(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;"))
    private Vec3d gildedglory$chainedVelocity(Vec3d pos, Vec3d velocity, Operation<Vec3d> original) {
        Entity entity = (Entity) (Object) this;

        if (entity instanceof LivingEntity && ModComponents.CHAINED.get(entity).getDuration() > 0) {
            World world = entity.getWorld();
            UUID uuid = ModComponents.CHAINED.get(entity).getCounterpart();
            Entity counterpart = GildedGloryUtil.getEntityFromUuid(uuid, world);

            if (counterpart != null) {
                velocity = GildedGloryUtil.adjustVelocity(entity.getPos(), counterpart.getPos().add(counterpart.getVelocity()), velocity, 10.0);
            }
        }
        return original.call(pos, velocity);
    }

    @WrapOperation(method = "updateVelocity", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;add(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;"))
    private Vec3d gildedglory$preventMovement(Vec3d pos, Vec3d velocity, Operation<Vec3d> original) {
        Entity entity = (Entity) (Object) this;

        if (entity instanceof LivingEntity && ModComponents.FOOLS_STATUE.get(entity).getDuration() > 0) {
            velocity = Vec3d.ZERO;
        }
        return original.call(pos, velocity);
    }

    @Inject(method = "handleAttack", at = @At(value = "RETURN"), cancellable = true)
    private void gildedglory$preventAttack(Entity attacker, CallbackInfoReturnable<Boolean> cir) {
        if (attacker instanceof LivingEntity entity && entity.getMainHandStack().isOf(ModItems.SWORDSPEAR)) {
            cir.setReturnValue(ChargeableWeapon.getCharge(entity.getMainHandStack()) > 0);
        }
    }
}
