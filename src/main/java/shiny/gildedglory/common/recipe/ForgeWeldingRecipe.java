package shiny.gildedglory.common.recipe;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import shiny.gildedglory.common.registry.block.ModBlocks;
import shiny.gildedglory.common.registry.recipe.ModRecipeSerializers;
import shiny.gildedglory.common.registry.recipe.ModRecipeTypes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ForgeWeldingRecipe implements CraftingRecipe {

    private final DefaultedList<Ingredient> ingredients;
    private final ItemStack result;

    public ForgeWeldingRecipe(ItemStack result, DefaultedList<Ingredient> ingredients) {
        this.ingredients = ingredients;
        this.result = result;
    }

    @Override
    public CraftingRecipeCategory getCategory() {
        return CraftingRecipeCategory.MISC;
    }

    @Override
    public boolean matches(CraftingRecipeInput input, World world) {
        if (input.getStackCount() != this.ingredients.size()) {
            return false;
        }
        else {
            return input.getSize() == 1 && this.ingredients.size() == 1
                    ? this.ingredients.get(0).test(input.getStackInSlot(0))
                    : input.getRecipeMatcher().match(this, null);
        }
    }

    //From old version, use this if above method does not work
    public boolean match(RecipeInputInventory input) {
        boolean bl = false;
        int matches = 0;

        List<Ingredient> list = new ArrayList<>(this.ingredients);
        List<ItemStack> list1 = new ArrayList<>(input.getHeldStacks());

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
        return bl && matches == this.ingredients.size();
    }

    public int requiredCount(Ingredient ingredient) {
        int i = 0;
        for (Ingredient ingredient1 : this.ingredients) {
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
        for (Ingredient ingredient : this.ingredients) {
            if (ingredient.test(stack)) i++;
        }
        return i;
    }

    @Override
    public ItemStack craft(CraftingRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        return this.result.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return this.result;
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        return this.ingredients;
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(ModBlocks.HEATED_ANVIL);
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.FORGE_WELDING;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.FORGE_WELDING;
    }

    public static class Serializer implements RecipeSerializer<ForgeWeldingRecipe> {
        private static final MapCodec<ForgeWeldingRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                                ItemStack.VALIDATED_CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
                                Ingredient.DISALLOW_EMPTY_CODEC
                                        .listOf()
                                        .fieldOf("ingredients")
                                        .flatXmap(
                                                ingredients -> {
                                                    Ingredient[] ingredients2 = ingredients.stream().filter(ingredient -> !ingredient.isEmpty()).toArray(Ingredient[]::new);
                                                    if (ingredients2.length == 0) {
                                                        return DataResult.error(() -> "No ingredients for forge welding recipe");
                                                    } else {
                                                        return ingredients2.length > 9
                                                                ? DataResult.error(() -> "Too many ingredients for forge welding recipe")
                                                                : DataResult.success(DefaultedList.copyOf(Ingredient.EMPTY, ingredients2));
                                                    }
                                                },
                                                DataResult::success
                                        )
                                        .forGetter(recipe -> recipe.ingredients)
                        )
                        .apply(instance, ForgeWeldingRecipe::new)
        );
        public static final PacketCodec<RegistryByteBuf, ForgeWeldingRecipe> PACKET_CODEC = PacketCodec.ofStatic(
                ForgeWeldingRecipe.Serializer::write, ForgeWeldingRecipe.Serializer::read
        );

        @Override
        public MapCodec<ForgeWeldingRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, ForgeWeldingRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        private static ForgeWeldingRecipe read(RegistryByteBuf buf) {
            int i = buf.readVarInt();
            DefaultedList<Ingredient> defaultedList = DefaultedList.ofSize(i, Ingredient.EMPTY);
            defaultedList.replaceAll(empty -> Ingredient.PACKET_CODEC.decode(buf));
            ItemStack itemStack = ItemStack.PACKET_CODEC.decode(buf);

            return new ForgeWeldingRecipe(itemStack, defaultedList);
        }

        private static void write(RegistryByteBuf buf, ForgeWeldingRecipe recipe) {
            buf.writeVarInt(recipe.ingredients.size());
            for (Ingredient ingredient : recipe.ingredients) {
                Ingredient.PACKET_CODEC.encode(buf, ingredient);
            }
            ItemStack.PACKET_CODEC.encode(buf, recipe.result);
        }
    }
}
