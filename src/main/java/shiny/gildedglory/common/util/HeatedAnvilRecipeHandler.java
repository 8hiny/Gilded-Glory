package shiny.gildedglory.common.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import shiny.gildedglory.common.recipe.ForgeWeldingRecipe;
import shiny.gildedglory.common.registry.recipe.ModRecipeTypes;

import java.util.Iterator;
import java.util.Optional;

public class HeatedAnvilRecipeHandler {

    public static boolean compress(DefaultedList<ItemEntity> items, World world, BlockPos pos) {
        if (!world.isClient()) {
            items = merge(items);

            DefaultedList<ItemStack> ingredients = DefaultedList.of();
            int maxCount = 64;
            for (ItemEntity entity : items) {
                if (!entity.getStack().isEmpty()) {
                    ingredients.add(entity.getStack());
                    maxCount = Math.min(maxCount, entity.getStack().getCount());
                }
            }

            if (maxCount > 0) {
                CraftingRecipeInput input = CraftingRecipeInput.create(ingredients.size(), 1, ingredients);
                RecipeEntry<ForgeWeldingRecipe> entry = getMatchingRecipe(world, input);
                if (entry != null) {
                    ForgeWeldingRecipe recipe = entry.value();
                    if (recipe != null) {
                        for (ItemEntity entity : items) {
                            ItemStack stack = entity.getStack();
                            int requiredCount = recipe.requiredCount(stack);
                            if (requiredCount > 0) maxCount = Math.min(maxCount, stack.getCount() / requiredCount);
                            int remainder = stack.getCount() - (maxCount * requiredCount);

                            stack.setCount(remainder);
                            entity.setStack(stack);
                        }
                        ItemEntity result = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), recipe.craft(input, world.getRegistryManager()), 0, 0, 0);
                        result.getStack().setCount(maxCount);
                        world.spawnEntity(result);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static DefaultedList<ItemEntity> merge(DefaultedList<ItemEntity> items) {
        DefaultedList<ItemEntity> merged = DefaultedList.of();

        if (!items.isEmpty()) {
            for (Iterator<ItemEntity> iterator = items.iterator(); iterator.hasNext();) {
                ItemEntity entity = iterator.next();

                if (!entity.isRemoved() && !entity.getStack().isEmpty()) {
                    int count = count(items, entity);

                    entity.getStack().setCount(count);
                    merged.add(entity);
                }
            }
        }
        return merged;
    }

    public static int count(DefaultedList<ItemEntity> items, ItemEntity entity) {
        int i = 0;
        DefaultedList<ItemEntity> toRemove = DefaultedList.of();

        for (ItemEntity entity1 : items) {

            if (ItemStack.areItemsAndComponentsEqual(entity.getStack(), entity1.getStack())) {
                i += entity.getStack().getCount();

                if (entity != entity1) toRemove.add(entity1);
            }
        }

        items.removeAll(toRemove);
        toRemove.forEach(Entity::discard);
        return i;
    }

    public static RecipeEntry<ForgeWeldingRecipe> getMatchingRecipe(World world, CraftingRecipeInput input) {
        Optional<RecipeEntry<ForgeWeldingRecipe>> optional = world.getRecipeManager().getFirstMatch(ModRecipeTypes.FORGE_WELDING, input, world);
        return optional.orElse(null);
    }
}
