package shiny.gildedglory.common.util;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.util.Pair;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Comparator;
import java.util.List;

public class HeatedAnvilRecipeHandler {

    private static final Comparator<Item> comparator = Comparator.comparing(a -> a.getName().getString());
    public static final DefaultedList<Pair<DefaultedList<Item>, Item>> COMPRESSION_RECIPES = DefaultedList.of();

    public static void addCompressionRecipe(Item output, Item... input) {
        DefaultedList<Item> items = DefaultedList.of();

        items.addAll(List.of(input));
        COMPRESSION_RECIPES.add(new Pair<>(items, output));
    }

    public static boolean compress(DefaultedList<ItemEntity> items, World world, BlockPos pos) {
        DefaultedList<Item> ingredients = DefaultedList.of();

        int maxCount = 64;
        for (ItemEntity entity : items) {
            ingredients.add(entity.getStack().getItem());
            maxCount = Math.min(maxCount, entity.getStack().getCount());
        }

        int remainder = 0;
        boolean success = false;
        boolean singleStack = ingredients.size() == 1 && maxCount > 1;

        ingredients.sort(comparator);

        for (Pair<DefaultedList<Item>, Item> pair : COMPRESSION_RECIPES) {
            DefaultedList<Item> recipe = pair.getLeft();
            recipe.sort(comparator);

            if (singleStack && recipe.contains(ingredients.get(0)) && ingredients.containsAll(recipe)) {
                Item ingredientItem = ingredients.get(0);

                int requiredCount = countElements(recipe, ingredientItem);
                int ingredientCount = maxCount;
                maxCount = ingredientCount / requiredCount;
                remainder = ingredientCount % requiredCount;

                success = maxCount > 0;
            }

            if (recipe.equals(ingredients) || success) {
                ItemEntity result = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), pair.getRight().getDefaultStack());
                result.getStack().setCount(maxCount);
                world.spawnEntity(result);
                success = true;
                break;
            }
        }

        if (success) {
            for (ItemEntity entity : items) {
                entity.getStack().setCount(singleStack ? remainder : entity.getStack().getCount() - maxCount);
            }
        }
        return success;
    }

    public static int countElements(DefaultedList<Item> list, Item item) {
        int matches = 0;
        for (Item item1 : list) {
            if (item.equals(item1)) matches++;
        }
        return matches;
    }
}
