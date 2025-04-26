package shiny.gildedglory.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import shiny.gildedglory.common.item.AuradeusItem;
import shiny.gildedglory.common.item.SickleItem;

@Mixin(Enchantment.class)
public abstract class EnchantmentTargetMixin {

    @Shadow @Final public EnchantmentTarget target;

    @Inject(method = "isAcceptableItem", at = @At(value = "RETURN"), cancellable = true)
    public void gildedglory$isAcceptableItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        Item item = stack.getItem();

        if (!cir.getReturnValue()) {
            if (item instanceof AuradeusItem) cir.setReturnValue(this.target == EnchantmentTarget.WEAPON || this.target == EnchantmentTarget.DIGGER);
        }
        else {
            if (item instanceof SickleItem) cir.setReturnValue(this.target == EnchantmentTarget.DIGGER);
        }
    }
}
