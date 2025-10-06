package shiny.gildedglory.common.item.custom;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

///An interface which can be implemented by other Item classes, which prevents them from affecting movement speed while being used.
public interface SprintUsableItem {

    public boolean allowSprinting(PlayerEntity user, ItemStack stack);
}
