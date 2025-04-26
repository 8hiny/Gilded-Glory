package shiny.gildedglory.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shiny.gildedglory.common.registry.component.ModComponents;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    //Overrides teleportation for players separately, as the ServerPlayerEntity class overrides the requestTeleport method
    @Inject(method = "requestTeleport", at = @At(value = "HEAD"), cancellable = true)
    private void gildedglory$preventTeleportAndBreakChains(double destX, double destY, double destZ, CallbackInfo ci) {
        PlayerEntity thisPlayer = this;
        if (thisPlayer instanceof ServerPlayerEntity) {
            if (ModComponents.CHAINED.get(thisPlayer).getDuration() > 0) {
                ModComponents.CHAINED.get(thisPlayer).unChain();
                ci.cancel();
            }
        }
    }
}
