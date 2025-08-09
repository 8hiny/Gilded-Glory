package shiny.gildedglory.common.registry.recipe;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import shiny.gildedglory.GildedGlory;
import shiny.gildedglory.common.recipe.oldForgeWeldingRecipe;

public class ModRecipes {

    public static final RecipeSerializer<oldForgeWeldingRecipe> FORGE_WELDING = register("forge_welding", new oldForgeWeldingRecipe.Serializer());

    public static <S extends RecipeSerializer<T>, T extends Recipe<?>> S register(String name, S serializer) {
        return Registry.register(Registries.RECIPE_SERIALIZER, GildedGlory.id(name), serializer);
    }

    public static void registerModRecipes() {

    }
}
