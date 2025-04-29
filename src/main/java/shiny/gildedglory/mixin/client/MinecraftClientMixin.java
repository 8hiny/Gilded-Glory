package shiny.gildedglory.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import shiny.gildedglory.common.registry.component.ModComponents;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Shadow @Nullable public ClientPlayerEntity player;

    @ModifyReturnValue(method = "hasOutline", at = @At(value = "RETURN"))
    private boolean gildedglory$highlightIraedeusTarget(boolean original, Entity entity) {
        if (this.player != null) {
            if (entity == ModComponents.IRAEDEUS.get(this.player).getTargetedEntity()) {
                return true;
            }
        }
        return original;
    }
}
