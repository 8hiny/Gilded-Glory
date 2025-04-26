package shiny.gildedglory.common.registry.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import shiny.gildedglory.GildedGlory;
import shiny.gildedglory.common.enchantment.ChainedEnchantment;
import shiny.gildedglory.common.enchantment.SolarFlareEnchantment;

public class ModEnchantments {

    public static Enchantment CHAINED = register("enma", new ChainedEnchantment(Enchantment.Rarity.VERY_RARE, EnchantmentTarget.WEAPON, EquipmentSlot.MAINHAND));
    public static Enchantment SOLAR_FLARE = register("solar_flare", new SolarFlareEnchantment(Enchantment.Rarity.VERY_RARE, EnchantmentTarget.WEAPON, EquipmentSlot.MAINHAND));

    private static Enchantment register(String name, Enchantment enchantment) {
        return Registry.register(Registries.ENCHANTMENT, GildedGlory.id(name), enchantment);
    }

    public static void registerModEnchantments() {
    }
}
