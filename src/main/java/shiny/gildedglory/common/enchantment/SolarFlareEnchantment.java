package shiny.gildedglory.common.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import shiny.gildedglory.common.registry.item.ModItems;

public class SolarFlareEnchantment extends Enchantment {

    public SolarFlareEnchantment(Rarity weight, EnchantmentTarget target, EquipmentSlot... slotTypes) {
        super(weight, target, slotTypes);
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return stack.isOf(ModItems.SWORDSPEAR);
    }

    @Override
    public boolean isTreasure() {
        return true;
    }
}
