package shiny.gildedglory.common.registry.damage_type;

import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import shiny.gildedglory.GildedGlory;

public class ModDamageTypes {

    public static final RegistryKey<DamageType> SLASH = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, GildedGlory.id("slash"));
    public static final RegistryKey<DamageType> SICKLE_CRIT = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, GildedGlory.id("sickle_crit"));
    public static final RegistryKey<DamageType> BEAM = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, GildedGlory.id("beam"));
    public static final RegistryKey<DamageType> IRAEDEUS = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, GildedGlory.id("iraedeus"));
    public static final RegistryKey<DamageType> SLASHED_AREA = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, GildedGlory.id("slashed_area"));
}
