package shiny.gildedglory.common.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import shiny.gildedglory.common.registry.recipe.ModRecipeTypes;
import shiny.gildedglory.common.registry.recipe.ModRecipes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ForgeWeldingRecipe implements Recipe<RecipeInputInventory> {

    private final Identifier id;
    private final ItemStack output;
    private final DefaultedList<Ingredient> input;

    public ForgeWeldingRecipe(Identifier id, DefaultedList<Ingredient> input, ItemStack output) {
        this.id = id;
        this.output = output;
        this.input = input;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public boolean matches(RecipeInputInventory input, World world) {
        return this.match(input);
    }

    public boolean match(RecipeInputInventory input) {
        boolean bl = false;
        int matches = 0;

        List<Ingredient> list = new ArrayList<>(this.input);
        List<ItemStack> list1 = new ArrayList<>(input.getInputStacks());

        if (!list.isEmpty()) {
            for (Iterator<Ingredient> iterator = list.iterator(); iterator.hasNext();) {
                Ingredient ingredient = iterator.next();
                int requiredCount = this.requiredCount(ingredient);

                boolean bl1 = false;
                if (!list1.isEmpty()) {
                    for (Iterator<ItemStack> iterator1 = list1.iterator(); iterator1.hasNext();) {
                        ItemStack stack = iterator1.next();

                        if (ingredient.test(stack)) {
                            bl = true;
                            if (stack.getCount() >= requiredCount) {
                                matches += requiredCount;
                            }
                            else {
                                matches++;
                            }

                            iterator1.remove();
                            bl1 = true;
                            break;
                        }
                    }
                }
                if (bl1) iterator.remove();
            }
        }
        return bl && matches == this.input.size();
    }

    public int requiredCount(Ingredient ingredient) {
        int i = 0;
        for (Ingredient ingredient1 : this.input) {
            for (int j = 0; j < ingredient1.getMatchingStacks().length; j++) {
                if (ingredient.test(ingredient1.getMatchingStacks()[j])) {
                    i++;
                    break;
                }
            }
        }
        return i;
    }

    public int requiredCount(ItemStack stack) {
        int i = 0;
        for (Ingredient ingredient : this.input) {
            if (ingredient.test(stack)) i++;
        }
        return i;
    }

    public int getRemainder(ItemStack stack) {
        int i = 0;
        for (Ingredient ingredient : this.input) {
            if (ingredient.test(stack)) i++;
        }
        return i;
    }

    @Override
    public ItemStack craft(RecipeInputInventory input, DynamicRegistryManager manager) {
        return this.output.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager manager) {
        return this.output;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.FORGE_WELDING;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.FORGE_WELDING;
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
        return true;
    }

    public static class Serializer implements RecipeSerializer<ForgeWeldingRecipe> {

        public ForgeWeldingRecipe read(Identifier identifier, JsonObject jsonObject) {
            DefaultedList<Ingredient> defaultedList = getIngredients(JsonHelper.getArray(jsonObject, "ingredients"));

            if (defaultedList.isEmpty()) {
                throw new JsonParseException("No ingredients for forge welding recipe");
            }
            else {
                ItemStack itemStack = ShapedRecipe.outputFromJson(JsonHelper.getObject(jsonObject, "result"));
                return new ForgeWeldingRecipe(identifier, defaultedList, itemStack);
            }
        }

        private static DefaultedList<Ingredient> getIngredients(JsonArray json) {
            DefaultedList<Ingredient> defaultedList = DefaultedList.of();

            for (int i = 0; i < json.size(); i++) {
                Ingredient ingredient = Ingredient.fromJson(json.get(i), false);
                if (!ingredient.isEmpty()) {
                    defaultedList.add(ingredient);
                }
            }

            return defaultedList;
        }

        public ForgeWeldingRecipe read(Identifier identifier, PacketByteBuf packetByteBuf) {
            int i = packetByteBuf.readVarInt();
            DefaultedList<Ingredient> defaultedList = DefaultedList.ofSize(i, Ingredient.EMPTY);

            defaultedList.replaceAll(ignored -> Ingredient.fromPacket(packetByteBuf));

            ItemStack itemStack = packetByteBuf.readItemStack();
            return new ForgeWeldingRecipe(identifier, defaultedList, itemStack);
        }

        public void write(PacketByteBuf packetByteBuf, ForgeWeldingRecipe recipe) {
            packetByteBuf.writeVarInt(recipe.input.size());
            for (Ingredient ingredient : recipe.input) {
                ingredient.write(packetByteBuf);
            }
            packetByteBuf.writeItemStack(recipe.output);
        }
    }
}
