package shiny.gildedglory.common.registry.item;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;
import shiny.gildedglory.GildedGlory;
import vectorwing.farmersdelight.common.registry.ModEffects;

public class FoodValues {

    public static final FoodComponent DR_PEPPER;
    public static final FoodComponent LIQUID_GOLD;
    public static final FoodComponent GOLDEN_BURGER;
    public static final FoodComponent GOLDEN_PASTA;
    public static final FoodComponent FOOLS_STEW;

    static {
        DR_PEPPER = new FoodComponent.Builder()
                .hunger(6)
                .saturationModifier(0.5f)
                .statusEffect(new StatusEffectInstance(StatusEffects.SPEED, 400, 1), 1.0f)
                .build();
        LIQUID_GOLD = new FoodComponent.Builder()
                .hunger(4)
                .saturationModifier(0.5f)
                .build();
        GOLDEN_BURGER = new FoodComponent.Builder()
                .hunger(16)
                .saturationModifier(0.75f)
                .statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 160, 1), 1.0f)
                .statusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 200, 0), 1.0f)
                .build();
        GOLDEN_PASTA = addNourishment(new FoodComponent.Builder()
                .hunger(14)
                .saturationModifier(0.75f),
                3600, 0)
                .build();
        FOOLS_STEW = addComfort(new FoodComponent.Builder()
                .hunger(10)
                .saturationModifier(0.6f),
                6000, 0)
                .build();
    }

    private static FoodComponent.Builder addComfort(FoodComponent.Builder builder, int duration, int amplifier) {
        if (GildedGlory.FARMERS_DELIGHT_INSTALLED) {
            builder = addStatusEffect(builder, ModEffects.COMFORT.get(), duration, amplifier);
        }
        return builder;
    }

    private static FoodComponent.Builder addNourishment(FoodComponent.Builder builder, int duration, int amplifier) {
        if (GildedGlory.FARMERS_DELIGHT_INSTALLED) {
            builder = addStatusEffect(builder, ModEffects.NOURISHMENT.get(), duration, amplifier);
        }
        return builder;
    }

    private static FoodComponent.Builder addStatusEffect(FoodComponent.Builder builder, StatusEffect effect, int duration, int amplifier) {
        return builder.statusEffect(new StatusEffectInstance(effect, duration, amplifier), 1.0f);
    }
}
