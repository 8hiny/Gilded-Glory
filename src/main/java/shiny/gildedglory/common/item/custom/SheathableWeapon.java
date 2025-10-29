package shiny.gildedglory.common.item.custom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import shiny.gildedglory.common.registry.data_component.ModComponentTypes;

///An interface which can be implemented by other Item classes, which causes a specific item to render in the off hand when this item is held.
public interface SheathableWeapon {

    /// Returns whether to render the sheath in the offhand or not. The sheath only renders when the item is not sheathed.
    public boolean isSheathed(LivingEntity holder, ItemStack stack);

    /// Returns whether the item is currently being sheathed.
    public boolean currentlySheathing(LivingEntity holder, ItemStack stack);

    /// Returns which item should be rendered in the offhand as the sheath.
    public Item getSheath(ItemStack stack);

    public SoundEvent getSheathSound(ItemStack stack);

    public int sheathTime();

    public static void setSheathed(ItemStack stack, boolean sheathed) {
        stack.set(ModComponentTypes.SHEATHED, sheathed);
    }
}
