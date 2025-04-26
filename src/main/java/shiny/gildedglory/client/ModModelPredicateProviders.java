package shiny.gildedglory.client;

import net.minecraft.client.item.ModelPredicateProviderRegistry;
import shiny.gildedglory.GildedGlory;
import shiny.gildedglory.common.item.ChargeableWeapon;
import shiny.gildedglory.common.item.CharmItem;
import shiny.gildedglory.common.registry.item.ModItems;

public class ModModelPredicateProviders {

    public static void registerModelPredicateProviders() {
        ModelPredicateProviderRegistry.register(
                ModItems.AURADEUS,
                GildedGlory.id("pull"),
                (stack, world, entity, seed) -> entity != null && entity.getActiveItem() == stack ? (stack.getMaxUseTime() - entity.getItemUseTimeLeft()) / 20.0f : 0.0f
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
                ModItems.SWORDSPEAR,
                GildedGlory.id("charge"),
                (stack, world, entity, seed) -> entity != null ? ChargeableWeapon.getCharge(stack) / 20.0f : 0.0f
        );
        ModelPredicateProviderRegistry.register(
                ModItems.SWORDSPEAR,
                GildedGlory.id("pull"),
                (stack, world, entity, seed) -> entity != null && entity.getActiveItem() == stack ? (stack.getMaxUseTime() - entity.getItemUseTimeLeft()) / 20.0f : 0.0f
        );
    }
}
