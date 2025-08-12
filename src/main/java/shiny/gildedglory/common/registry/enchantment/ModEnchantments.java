package shiny.gildedglory.common.registry.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.*;
import shiny.gildedglory.GildedGlory;

public class ModEnchantments {

    public static RegistryKey<Enchantment> ENMA = RegistryKey.of(RegistryKeys.ENCHANTMENT, GildedGlory.id("enma"));
    public static RegistryKey<Enchantment> SOLAR_FLARE = RegistryKey.of(RegistryKeys.ENCHANTMENT, GildedGlory.id("solar_flare"));
}
