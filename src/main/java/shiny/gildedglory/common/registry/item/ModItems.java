package shiny.gildedglory.common.registry.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import shiny.gildedglory.GildedGlory;
import shiny.gildedglory.common.item.*;
import shiny.gildedglory.common.item.compat.CompatConsumableItem;

public class ModItems {

    public static final Item TWISTEEL_INGOT = register("twisteel_ingot", new Item(new FabricItemSettings().fireproof()));
    public static final Item GLOOMETAL_INGOT = register("gloometal_ingot", new Item(new FabricItemSettings().fireproof()));
    public static final Item FOOLS_GOLD_INGOT = register("fools_gold_ingot", new Item(new FabricItemSettings()));
    public static final Item AURADEUS = register("auradeus", new AuradeusItem(ModToolMaterials.TWISTEEL, 2.5f, -2.6f, new FabricItemSettings().fireproof()));
    public static final Item TWISTEEL_SICKLE = register("twisteel_sickle", new SickleItem(ModToolMaterials.TWISTEEL, 2, -2.3f, new FabricItemSettings().fireproof()));
    public static final Item TWISTEEL_CHARM = register("twisteel_charm", new CharmItem(new FabricItemSettings().maxCount(1).fireproof()));
    public static final Item GILDED_HORN = register("gilded_horn", new GildedHornItem(new FabricItemSettings().maxCount(1)));
    public static final Item SWORDSPEAR = register("swordspear", new SwordSpearItem(ModToolMaterials.SWORDSPEAR, 4, -2.9f, new FabricItemSettings()));
    public static final Item IRAEDEUS = register("iraedeus", new IraedeusItem(ModToolMaterials.GLOOMETAL, 3, -2.4f, new FabricItemSettings()));

    public static final Item FOOLS_GOLD_HELMET = register("fools_gold_helmet", new FoolsArmorItem(ModArmorMaterials.FOOLS_GOLD, ArmorItem.Type.HELMET, new FabricItemSettings()));
    public static final Item FOOLS_GOLD_CHESTPLATE = register("fools_gold_chestplate", new FoolsArmorItem(ModArmorMaterials.FOOLS_GOLD, ArmorItem.Type.CHESTPLATE, new FabricItemSettings()));
    public static final Item FOOLS_GOLD_LEGGINGS = register("fools_gold_leggings", new FoolsArmorItem(ModArmorMaterials.FOOLS_GOLD, ArmorItem.Type.LEGGINGS, new FabricItemSettings()));
    public static final Item FOOLS_GOLD_BOOTS = register("fools_gold_boots", new FoolsArmorItem(ModArmorMaterials.FOOLS_GOLD, ArmorItem.Type.BOOTS, new FabricItemSettings()));

    public static final Item DR_PEPPER = register("dr_pepper", new CompatConsumableItem(new FabricItemSettings().maxCount(16).recipeRemainder(Items.GLASS_BOTTLE)
            .food(FoodValues.DR_PEPPER),
            32, Items.GLASS_BOTTLE
    ));
    public static final Item LIQUID_GOLD = register("liquid_gold", new CompatConsumableItem(new FabricItemSettings().maxCount(16).recipeRemainder(Items.GLASS_BOTTLE)
            .food(FoodValues.LIQUID_GOLD),
            32 , Items.GLASS_BOTTLE
    ));
    public static final Item GOLDEN_PASTA = register("golden_pasta", new CompatConsumableItem(new FabricItemSettings().maxCount(16).recipeRemainder(Items.BOWL)
            .food(FoodValues.GOLDEN_PASTA),
            32 , Items.BOWL
    ));
    public static final Item FOOLS_STEW = register("fools_stew", new CompatConsumableItem(new FabricItemSettings().maxCount(16).recipeRemainder(Items.BOWL)
            .food(FoodValues.FOOLS_STEW),
            32 , Items.BOWL
    ));
    public static final Item GOLDEN_BURGER = register("golden_burger", new CompatConsumableItem(new FabricItemSettings().food(FoodValues.GOLDEN_BURGER)));


    private static void addToCombatItemGroup(FabricItemGroupEntries entries) {
        entries.addAfter(Items.TRIDENT, AURADEUS);
        entries.addAfter(Items.TRIDENT, TWISTEEL_SICKLE);
        entries.addAfter(Items.TRIDENT, TWISTEEL_CHARM);
        entries.addAfter(Items.TRIDENT, GILDED_HORN);
        entries.addAfter(Items.TRIDENT, SWORDSPEAR);
        entries.addAfter(Items.TRIDENT, IRAEDEUS);
        entries.addAfter(Items.TURTLE_HELMET, FOOLS_GOLD_BOOTS);
        entries.addAfter(Items.TURTLE_HELMET, FOOLS_GOLD_LEGGINGS);
        entries.addAfter(Items.TURTLE_HELMET, FOOLS_GOLD_CHESTPLATE);
        entries.addAfter(Items.TURTLE_HELMET, FOOLS_GOLD_HELMET);
    }

    private static void addToIngredientItemGroup(FabricItemGroupEntries entries) {
        entries.addAfter(Items.NETHERITE_INGOT, TWISTEEL_INGOT);
        entries.addAfter(Items.NETHERITE_INGOT, GLOOMETAL_INGOT);
        entries.addAfter(Items.NETHERITE_INGOT,FOOLS_GOLD_INGOT);
    }

    private static Item register(String name, Item item) {
        return Registry.register(Registries.ITEM, GildedGlory.id(name), item);
    }

    public static void registerModItems() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(ModItems::addToCombatItemGroup);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(ModItems::addToIngredientItemGroup);
    }
}
