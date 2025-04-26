package shiny.gildedglory.common.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;

public interface CustomAttackWeapon {

    CustomAttackData onAttack(ItemStack stack, LivingEntity attacker, Entity target, DamageSource source, float amount);

    record CustomAttackData(ItemStack stack, LivingEntity attacker, Entity target, DamageSource source, float amount, boolean successful) {
    }
}
