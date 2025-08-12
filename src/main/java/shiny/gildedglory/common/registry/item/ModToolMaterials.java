package shiny.gildedglory.common.registry.item;

import com.google.common.base.Suppliers;
import net.minecraft.block.Block;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;

import java.util.function.Supplier;

public enum ModToolMaterials implements ToolMaterial {

    TWISTEEL(BlockTags.INCORRECT_FOR_NETHERITE_TOOL,1800, 9.0f, 4.0f, 15, () -> Ingredient.ofItems(ModItems.TWISTEEL_INGOT)),
    GLOOMETAL(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 1600, 10.0f, 4.0f, 18, () -> Ingredient.ofItems(ModItems.GLOOMETAL_INGOT)),
    FOOLS_GOLD(BlockTags.INCORRECT_FOR_GOLD_TOOL, 200, 11.0f, 1.0f, 20, () -> Ingredient.ofItems(ModItems.FOOLS_GOLD_INGOT)),
    SWORDSPEAR(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 1400, 12.0f, 4.0f, 22, () -> Ingredient.ofItems(Items.GOLD_INGOT));

    private final TagKey<Block> inverseTag;
    private final int itemDurability;
    private final float miningSpeed;
    private final float attackDamage;
    private final int enchantability;
    private final Supplier<Ingredient> repairIngredient;

    private ModToolMaterials(
            final TagKey<Block> inverseTag,
            final int itemDurability,
            final float miningSpeed,
            final float attackDamage,
            final int enchantability,
            final Supplier<Ingredient> repairIngredient
    ) {
        this.inverseTag = inverseTag;
        this.itemDurability = itemDurability;
        this.miningSpeed = miningSpeed;
        this.attackDamage = attackDamage;
        this.enchantability = enchantability;
        this.repairIngredient = Suppliers.memoize(repairIngredient::get);
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
    public TagKey<Block> getInverseTag() {
        return this.inverseTag;
    }

    @Override
    public int getEnchantability() {
        return this.enchantability;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return (Ingredient)this.repairIngredient.get();
    }
}
