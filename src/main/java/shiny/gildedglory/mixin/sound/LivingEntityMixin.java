package shiny.gildedglory.mixin.sound;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import shiny.gildedglory.common.item.ChargeableWeapon;
import shiny.gildedglory.common.registry.item.ModItems;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends EntityMixin {

    @Override
    public float getStress() {
        LivingEntity entity = (LivingEntity) (Object) this;
        ItemStack stack = entity.getMainHandStack();

        if (stack.isOf(ModItems.IRAEDEUS)) {
            float delta = ChargeableWeapon.getChargePercentage(entity.getMainHandStack());
            return 1.0f - (delta * delta);
        }
        return super.getStress();
    }
}
