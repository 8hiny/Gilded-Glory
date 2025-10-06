package shiny.gildedglory.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.ItemStack;
import shiny.gildedglory.GildedGlory;
import shiny.gildedglory.client.util.SheathedInventoryModelPredicateHelper;
import shiny.gildedglory.common.item.custom.ChargeableWeapon;
import shiny.gildedglory.common.item.CharmItem;
import shiny.gildedglory.common.item.custom.SheathableWeapon;
import shiny.gildedglory.common.registry.item.ModItems;

public class ModModelPredicateProviders {

    public static void registerModelPredicateProviders() {
        ModelPredicateProviderRegistry.register(
                ModItems.AURADEUS,
                GildedGlory.id("pull"),
                (stack, world, entity, seed) -> entity != null && entity.getActiveItem() == stack ? (stack.getMaxUseTime(entity) - entity.getItemUseTimeLeft()) / 20.0f : 0.0f
        );
        ModelPredicateProviderRegistry.register(
                ModItems.GILDED_HORN,
                GildedGlory.id("tooting"),
                (stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0f : 0.0f
        );
        ModelPredicateProviderRegistry.register(
                ModItems.TWISTEEL_CHARM,
                GildedGlory.id("has_owner"),
                (stack, world, entity, seed) -> CharmItem.hasOwner(stack) ? 1.0f : 0.0f
        );
        ModelPredicateProviderRegistry.register(
                GildedGlory.id("charge_percentage"),
                (stack, world, entity, seed) -> ChargeableWeapon.getChargePercentage(stack)
        );
        ModelPredicateProviderRegistry.register(
                ModItems.SWORDSPEAR,
                GildedGlory.id("pull"),
                (stack, world, entity, seed) -> entity != null && entity.getActiveItem() == stack ? (stack.getMaxUseTime(entity) - entity.getItemUseTimeLeft()) / 20.0f : 0.0f
        );
        ModelPredicateProviderRegistry.register(
                GildedGlory.id("sheathed"),
                (stack, world, entity, seed) -> {
                    if (entity != null && stack.getItem() instanceof SheathableWeapon weapon && weapon.isSheathed(entity, stack)) {
                        return 1.0f;
                    }
                    else if (entity == null) {
                        return 1.0f;
                    }
                    return 0.0f;
                }
        );
        ModelPredicateProviderRegistry.register(
                GildedGlory.id("inventory_sheathed"),
                (stack, world, entity, seed) -> {
                    boolean bl = entity != null && (entity.getMainHandStack() == stack || entity.getOffHandStack() == stack);
                    if (MinecraftClient.getInstance().currentScreen instanceof SheathedInventoryModelPredicateHelper helper) {
                        ItemStack hoveredStack = helper.getHoveredStack();
                        boolean bl1 = hoveredStack != null && hoveredStack == stack;

                        if (entity == null) {
                            if (bl1) return 0.0f;
                        }
                        else if (stack.getItem() instanceof SheathableWeapon weapon) {
                            if (!weapon.isSheathed(entity, stack) && (bl || bl1)) {
                                return 0.0f;
                            }
                        }
                    }
                    else if (bl && stack.getItem() instanceof SheathableWeapon weapon && !weapon.isSheathed(entity, stack)) {
                        return 0.0f;
                    }
                    return 1.0f;
                }
        );
    }
}
