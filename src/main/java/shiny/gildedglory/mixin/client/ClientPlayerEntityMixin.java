package shiny.gildedglory.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import shiny.gildedglory.GildedGlory;
import shiny.gildedglory.client.util.GildedGloryUtil;
import shiny.gildedglory.common.item.SprintUsableItem;
import shiny.gildedglory.common.registry.particle.ModParticles;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {

    @Shadow protected abstract boolean isWalking();
    @Shadow protected abstract boolean canSprint();
    @Shadow protected abstract boolean canVehicleSprint(Entity vehicle);

    @Shadow @Final protected MinecraftClient client;

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @WrapOperation(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"))
    private boolean gildedglory$allowSprintWithItem(ClientPlayerEntity player, Operation<Boolean> original) {
        return (original.call(player) && !(player.getActiveItem().getItem() instanceof SprintUsableItem));
    }

    @Inject(method = "canStartSprinting", at = @At(value = "RETURN"), cancellable = true)
    private void gildedglory$canStartSprintWithItem(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(cir.getReturnValue() || (!this.isSprinting()
                && this.isWalking()
                && this.canSprint()
                && this.getActiveItem().getItem() instanceof SprintUsableItem
                && !this.hasStatusEffect(StatusEffects.BLINDNESS)
                && (!this.hasVehicle() || this.canVehicleSprint(this.getVehicle()))
                && !this.isFallFlying()));
    }

//    @Inject(method = "tick", at = @At(value = "TAIL"))
//    private void gildedglory$addCosmeticPlayerParticles(CallbackInfo ci) {
//        ClientWorld world = (ClientWorld) this.getWorld();
//        PlayerEntity shiny = world.getPlayerByUuid(GildedGlory.SHINY_UUID);
//
//        double offsetX = this.random.nextGaussian() * 0.35;
//        double offsetY = this.random.nextGaussian() * 0.4;
//        double offsetZ = this.random.nextGaussian() * 0.35;
//
//        if (Math.random() < 0.175 && shiny != null) {
//            GildedGloryUtil.addPersonalParticles(shiny, ModParticles.SPARKLE, shiny.getX() + offsetX, shiny.getBodyY(0.5) + offsetY, shiny.getZ() + offsetZ, 0, 0, 0);
//        }
//    }
}
