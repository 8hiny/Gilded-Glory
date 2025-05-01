package shiny.gildedglory.common.registry.recipe;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import shiny.gildedglory.GildedGlory;
import shiny.gildedglory.common.recipe.ForgeWeldingRecipe;

public class ModRecipeTypes {

    public static final RecipeType<ForgeWeldingRecipe> FORGE_WELDING = register("forge_welding");

    public static <T extends Recipe<?>> RecipeType<T> register(String name) {
        return Registry.register(Registries.RECIPE_TYPE, GildedGlory.id(name), new RecipeType<T>() {
            public String toString() {
                return name;
            }
        });
    }

    public static void registerModRecipeTypes() {

    }
}
