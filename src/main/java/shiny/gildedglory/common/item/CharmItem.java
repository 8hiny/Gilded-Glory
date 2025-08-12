package shiny.gildedglory.common.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import shiny.gildedglory.common.registry.data_component.ModComponentTypes;
import shiny.gildedglory.common.registry.item.ModItems;

import java.util.List;
import java.util.UUID;

public class CharmItem extends Item {

    //TODO Add more uses for this

    public CharmItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (user.isSneaking() && isOwner(stack, user)) {
            clearOwner(stack);
            return TypedActionResult.success(stack);
        }
        else if (!user.isSneaking() && !hasOwner(stack)){
            setOwner(stack, user);
            return TypedActionResult.success(stack);
        }
        return TypedActionResult.fail(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if (hasOwner(stack) && !isOwner(stack, entity)) {
            ItemEntity droppedItem = entity.dropStack(stack.copy());
            if (droppedItem != null) {
                droppedItem.setPickupDelay(40);
                stack.decrement(1);
            }
        }
    }

    public static void setOwner(ItemStack stack, Entity owner) {
        stack.set(ModComponentTypes.OWNER_NAME, owner.getDisplayName().getString());
        stack.set(ModComponentTypes.OWNER_UUID, owner.getUuid());
    }

    public static void clearOwner(ItemStack stack) {
        stack.remove(ModComponentTypes.OWNER_UUID);
        stack.remove(ModComponentTypes.OWNER_NAME);
    }

    public static boolean hasOwner(ItemStack stack) {
        return stack.contains(ModComponentTypes.OWNER_UUID);
    }

    public static boolean isOwner(ItemStack stack, Entity entity) {
        return getOwnerUUID(stack) != null && entity.getUuid().equals(getOwnerUUID(stack));
    }

    public static UUID getOwnerUUID(ItemStack stack) {
        return stack.get(ModComponentTypes.OWNER_UUID);
    }

    public static String getOwnerName(ItemStack stack) {
        String name = stack.get(ModComponentTypes.OWNER_NAME);
        return name != null ? name : "";
    }

    public static boolean hasOwnedCharm(PlayerEntity holder) {
        PlayerInventory inventory = holder.getInventory();
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);

            if (stack.isOf(ModItems.TWISTEEL_CHARM) && isOwner(stack, holder)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Text getName(ItemStack stack) {
        return Text.translatable(this.getTranslationKey(stack)).setStyle(Style.EMPTY.withColor(0xCC495C));
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("tooltip.gildedglory.charm_0").formatted(Formatting.GRAY));
        if (hasOwner(stack)) {
            tooltip.add(Text.translatable("tooltip.gildedglory.charm_1").formatted(Formatting.GRAY).append(Text.literal(getOwnerName(stack)).setStyle(Style.EMPTY.withColor(0xCC495C))));
        }
    }
}
