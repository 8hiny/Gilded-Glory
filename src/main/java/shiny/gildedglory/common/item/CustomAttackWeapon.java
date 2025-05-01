package shiny.gildedglory.common.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;

///An interface which can be implemented by other Item classes, which allows them to add additional functionality to melee attacks.
public interface CustomAttackWeapon {

    CustomAttackData onAttack(ItemStack stack, LivingEntity attacker, Entity target, DamageSource source, float amount);

    record CustomAttackData(ItemStack stack, LivingEntity attacker, Entity target, DamageSource source, float amount, boolean successful) {
    }
}
