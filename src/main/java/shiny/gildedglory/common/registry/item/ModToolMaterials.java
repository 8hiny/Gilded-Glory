package shiny.gildedglory.common.registry.item;

import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Lazy;

import java.util.function.Supplier;

public enum ModToolMaterials implements ToolMaterial {

    TWISTEEL(4, 1800, 9.0f, 4.0f, 15, () -> Ingredient.ofItems(ModItems.TWISTEEL_INGOT)),
    GLOOMETAL(4, 1600, 10.0f, 4.0f, 18, () -> Ingredient.ofItems(ModItems.GLOOMETAL_INGOT)),
    FOOLS_GOLD(1, 200, 11.0f, 1.0f, 20, () -> Ingredient.ofItems(ModItems.FOOLS_GOLD_INGOT)),
    SWORDSPEAR(2, 1400, 12.0f, 4.0f, 22, () -> Ingredient.ofItems(Items.GOLD_INGOT));

    private final int miningLevel;
    private final int itemDurability;
    private final float miningSpeed;
    private final float attackDamage;
    private final int enchantability;
    private final Lazy<Ingredient> repairIngredient;

    ModToolMaterials(int miningLevel, int itemDurability, float miningSpeed, float attackDamage, int enchantability, Supplier<Ingredient> repairIngredient) {
        this.miningLevel = miningLevel;
        this.itemDurability = itemDurability;
        this.miningSpeed = miningSpeed;
        this.attackDamage = attackDamage;
        this.enchantability = enchantability;
        this.repairIngredient = new Lazy<>(repairIngredient);
    }

    @Override
    public int getDurability() {
        return this.itemDurability;
    }

    @Override
    public float getMiningSpeedMultiplier() {
        return this.miningSpeed;
    }

    @Override
    public float getAttackDamage() {
        return this.attackDamage;
    }

    @Override
    public int getMiningLevel() {
        return this.miningLevel;
    }

    @Override
    public int getEnchantability() {
        return this.enchantability;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }
}
