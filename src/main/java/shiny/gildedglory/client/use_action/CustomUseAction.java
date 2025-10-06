package shiny.gildedglory.client.use_action;

import net.minecraft.item.ItemStack;

import java.util.function.Function;

public class CustomUseAction implements BaseUseAction {

    private final Function<UseActionContext, ItemStack> itemTransformations;
    private final boolean applyBefore;

    public CustomUseAction(Function<UseActionContext, ItemStack> itemTransformations, boolean applyBefore) {
        this.itemTransformations = itemTransformations;
        this.applyBefore = applyBefore;
    }

    /** Modifies how a given item is rendered in first person when this use action is applied.
     * @return The final item to be rendered; Allows creating custom use actions which render alternate items.
     */
    public ItemStack applyFirstPersonTransforms(UseActionContext context) {
        return this.itemTransformations.apply(context);
    }

    /** Returns whether to apply transformations before the item equip offset is applied.
     * The item equip offset is the animation which plays when the item is first equipped.
     * It also translates the item based on whether it is held in the main or the offhand.
     */
    public boolean applyBefore() {
        return this.applyBefore;
    }

    @Override
    public Value value() {
        return Value.CUSTOM;
    }
}
