package shiny.gildedglory.common.recipe;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;

public class SimpleRecipeInventory implements RecipeInputInventory {

    private final DefaultedList<ItemStack> input;

    public SimpleRecipeInventory(DefaultedList<ItemStack> input) {
        this.input = input;
    }

    @Override
    public List<ItemStack> getInputStacks() {
        return List.copyOf(this.input);
    }

    @Override
    public int size() {
        return this.input.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.input) {
            if (!stack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.input.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(this.input, slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(this.input, slot, amount);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.input.set(slot, stack);
    }

    @Override
    public void markDirty() {
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void provideRecipeInputs(RecipeMatcher finder) {
        for (ItemStack stack : this.input) {
            finder.addUnenchantedInput(stack);
        }
    }

    @Override
    public void clear() {
        this.input.clear();
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }
}
