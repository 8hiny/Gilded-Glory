package shiny.gildedglory.common.registry.recipe;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import shiny.gildedglory.GildedGlory;
import shiny.gildedglory.common.recipe.ForgeWeldingRecipe;

public class ModRecipes {

    public static final RecipeSerializer<ForgeWeldingRecipe> FORGE_WELDING = register("forge_welding", new ForgeWeldingRecipe.Serializer());

    public static <S extends RecipeSerializer<T>, T extends Recipe<?>> S register(String name, S serializer) {
        return Registry.register(Registries.RECIPE_SERIALIZER, GildedGlory.id(name), serializer);
    }

    public static void registerModRecipes() {

    }
}
