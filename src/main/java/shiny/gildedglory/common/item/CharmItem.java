package shiny.gildedglory.common.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import shiny.gildedglory.common.registry.item.ModItems;

import java.util.List;
import java.util.UUID;

public class CharmItem extends Item {

    //TODO Add more uses for this

    private static final String OWNER_KEY = "gildedglory:owner";

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
            setOwner(stack, user.getEntityName(), user.getUuid());
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

    public static void setOwner(ItemStack stack, String name, UUID uuid) {
        NbtCompound nbt = stack.getOrCreateNbt();
        NbtCompound owner = new NbtCompound();

        owner.putString("Name", name);
        owner.putUuid("Uuid", uuid);
        nbt.put(OWNER_KEY, owner);
        stack.setNbt(nbt);
    }

    public static void clearOwner(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        if (nbt != null) {
            NbtCompound nbtCompound = nbt.getCompound(OWNER_KEY);

            nbtCompound.remove("Name");
            nbtCompound.remove("Uuid");
            stack.setNbt(nbtCompound);
        }
    }

    public static boolean hasOwner(ItemStack stack) {
        return stack.getNbt() != null && stack.getNbt().contains(OWNER_KEY);
    }

    public static boolean isOwner(ItemStack stack, Entity entity) {
        return stack.getNbt() != null && hasOwner(stack) && getOwnerUUID(stack) != null && entity.getUuid().equals(getOwnerUUID(stack));
        //if the above stops working again, remove the hasOwner check, but fix the crash it involves
    }

    public static UUID getOwnerUUID(ItemStack stack) {
        if (stack.getNbt() == null || !hasOwner(stack)) return null;
        return stack.getNbt().getCompound(OWNER_KEY).getUuid("Uuid");
    }

    public static String getOwnerName(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        if (nbt != null) {
            NbtCompound owner = nbt.getCompound(OWNER_KEY);
            return owner.getString("Name");
        }
        else return "";
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
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("tooltip.gildedglory.charm_0").formatted(Formatting.GRAY));
        if (hasOwner(stack)) {
            tooltip.add(Text.translatable("tooltip.gildedglory.charm_1").formatted(Formatting.GRAY).append(Text.literal(getOwnerName(stack)).setStyle(Style.EMPTY.withColor(0xCC495C))));
        }
    }
}
