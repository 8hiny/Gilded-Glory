package shiny.gildedglory.common.item.custom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import shiny.gildedglory.common.registry.data_component.ModComponentTypes;

/**
 * An interface for items which can hold charge.
 * Items which implement this interface are put on cooldown if their user is attacked while charging or consuming charge.
 * Also contains a few helper methods for related stuff.
 */
public interface ChargeableWeapon {

    default int getMinCharge() {
        return 0;
    }

    int getMaxCharge();

    /**
     * Returns whether the weapon can use charge when held in the offhand.
     */
    boolean offHandUsable();

    /**
     * Returns whether the weapon gains charge while being used.
     */
    boolean chargeWhileUsing();

    /**
     * Returns whether the weapon's charge value is only set once stopped using.
     */
    default boolean chargeSetOnStoppedUsing() {
        return false;
    }

    static void setCharge(ItemStack stack, int charge) {
        if (stack.getItem() instanceof ChargeableWeapon weapon) {
            charge = MathHelper.clamp(charge, weapon.getMinCharge(), weapon.getMaxCharge());

            if (charge > weapon.getMinCharge()) stack.set(ModComponentTypes.CHARGE, charge);
            else stack.remove(ModComponentTypes.CHARGE);
        }
    }

    static void addCharge(ItemStack stack, int charge) {
        setCharge(stack, getCharge(stack) + charge);
    }

    static int getCharge(ItemStack stack) {
        return stack.getOrDefault(ModComponentTypes.CHARGE, 0);
    }

    /**
     * Returns the amount of charge stored in either the entity's main or off hand item stacks. Prioritizes the main hand.
     */
    static int getCharge(LivingEntity holder) {
        ItemStack stack = get(holder);
        return stack != null ? getCharge(stack) : 0;
    }

    static float getChargePercentage(ItemStack stack) {
        if (stack.getItem() instanceof ChargeableWeapon weapon) {
            return (float) getCharge(stack) / weapon.getMaxCharge();
        }
        return 0.0f;
    }

    static boolean hasCharge(ItemStack stack) {
        if (stack.getItem() instanceof ChargeableWeapon weapon) {
            return getCharge(stack) > weapon.getMinCharge();
        }
        return false;
    }

    static boolean full(ItemStack stack) {
        if (stack.getItem() instanceof ChargeableWeapon weapon) {
            return getCharge(stack) == weapon.getMaxCharge();
        }
        return false;
    }

    static boolean hasCharge(LivingEntity holder) {
        ItemStack stack = get(holder);
        return stack != null && hasCharge(stack);
    }

    /**
     * Checks whether the item can lose charge. Effectively the same as checking if the item is being used.
     */
    default boolean canLoseCharge(ItemStack stack) {
        return hasCharge(stack);
    }

    static void tickCharge(ItemStack stack) {
        addCharge(stack, -1);
    }

    static ItemStack get(LivingEntity holder) {
        if (holder.getMainHandStack().getItem() instanceof ChargeableWeapon) {
            return holder.getMainHandStack();
        }
        else if (holder.getOffHandStack().getItem() instanceof ChargeableWeapon weapon && weapon.offHandUsable()) {
            return holder.getOffHandStack();
        }
        return null;
    }
}
