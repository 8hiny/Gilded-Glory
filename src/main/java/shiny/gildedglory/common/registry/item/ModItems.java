package shiny.gildedglory.common.registry.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import shiny.gildedglory.GildedGlory;
import shiny.gildedglory.GildedGloryClient;
import shiny.gildedglory.common.item.*;
import shiny.gildedglory.common.item.compat.CompatConsumableItem;
import shiny.gildedglory.common.item.custom.HiddenItem;

public class ModItems {

    public static final Item TWISTEEL_INGOT = register("twisteel_ingot", new Item(new Item.Settings().fireproof()));
    public static final Item GLOOMETAL_INGOT = register("gloometal_ingot", new Item(new Item.Settings().fireproof()));
    public static final Item FOOLS_GOLD_INGOT = register("fools_gold_ingot", new Item(new Item.Settings()));
    public static final Item TWISTEEL_CHARM = register("twisteel_charm", new CharmItem(new Item.Settings().maxCount(1).fireproof()));
    public static final Item GILDED_HORN = register("gilded_horn", new GildedHornItem(new Item.Settings().maxCount(1)));

    public static final Item AURADEUS = registerWithGui(
            "auradeus", new AuradeusItem(ModToolMaterials.TWISTEEL, new Item.Settings()
                    .fireproof()
                    .attributeModifiers(AuradeusItem.createAttributeModifiers(ModToolMaterials.TWISTEEL, 3, -2.6f, 0.5f))
            )
    );
    public static final Item SWORDSPEAR = registerWithGui(
            "swordspear", new SwordSpearItem(ModToolMaterials.SWORDSPEAR, new Item.Settings()
                    .fireproof()
                    .attributeModifiers(SwordSpearItem.createAttributeModifiers(ModToolMaterials.SWORDSPEAR, 4, -2.9f, 0.75f))
            )
    );
    public static final Item IRAEDEUS = registerWithGui(
            "iraedeus", new IraedeusItem(
                    ModToolMaterials.GLOOMETAL, new Item.Settings()
                    .fireproof()
                    .attributeModifiers(SheathableSwordItem.createAttributeModifiers(ModToolMaterials.GLOOMETAL, 3, -2.4f, 0.25f))
            )
    );
    public static final Item UNDIVINE_AXE = registerWithGui(
            "undivine_axe", new AxeItem(
                    ModToolMaterials.GLOOMETAL, new Item.Settings()
                    .fireproof()
                    .attributeModifiers(AxeItem.createAttributeModifiers(ModToolMaterials.GLOOMETAL, 5.0f, -3.0f))
            )
    );
    public static final Item TWISTEEL_SICKLE = register(
            "twisteel_sickle", new SickleItem(
                    ModToolMaterials.TWISTEEL, new Item.Settings()
                    .fireproof()
                    .attributeModifiers(SwordItem.createAttributeModifiers(ModToolMaterials.TWISTEEL, 2, -2.3f))
            )
    );
    public static final Item THROWABLE_WIP = registerWithGui(
            "throwable_wip", new ThrowableSwordItem(
                    ModToolMaterials.GLOOMETAL, new Item.Settings()
                    .fireproof()
                    .attributeModifiers(SwordItem.createAttributeModifiers(ModToolMaterials.GLOOMETAL, 3, -2.4f))
            )
    );
    public static final Item KATANA = registerWithGui(
            "katana", new KatanaItem(
                    ToolMaterials.NETHERITE, new Item.Settings()
                    .fireproof()
                    .attributeModifiers(SwordItem.createAttributeModifiers(ToolMaterials.NETHERITE, 3, -2.7f))
            )
    );

    public static final Item FOOLS_GOLD_HELMET = register(
            "fools_gold_helmet", new FoolsArmorItem(
                    ModArmorMaterials.FOOLS_GOLD, ArmorItem.Type.HELMET, new Item.Settings()
                    .component(EnchantmentEffectComponentTypes.PREVENT_ARMOR_CHANGE, Unit.INSTANCE)
            )
    );
    public static final Item FOOLS_GOLD_CHESTPLATE = register(
            "fools_gold_chestplate", new FoolsArmorItem(
                    ModArmorMaterials.FOOLS_GOLD, ArmorItem.Type.CHESTPLATE, new Item.Settings()
                    .component(EnchantmentEffectComponentTypes.PREVENT_ARMOR_CHANGE, Unit.INSTANCE)
            )
    );
    public static final Item FOOLS_GOLD_LEGGINGS = register(
            "fools_gold_leggings", new FoolsArmorItem(
                    ModArmorMaterials.FOOLS_GOLD, ArmorItem.Type.LEGGINGS, new Item.Settings()
                    .component(EnchantmentEffectComponentTypes.PREVENT_ARMOR_CHANGE, Unit.INSTANCE)
            )
    );
    public static final Item FOOLS_GOLD_BOOTS = register(
            "fools_gold_boots", new FoolsArmorItem(
                    ModArmorMaterials.FOOLS_GOLD, ArmorItem.Type.BOOTS, new Item.Settings()
                    .component(EnchantmentEffectComponentTypes.PREVENT_ARMOR_CHANGE, Unit.INSTANCE)
            )
    );

    public static final Item DR_PEPPER = registerWithGui("dr_pepper", new CompatConsumableItem(new Item.Settings().maxCount(16).recipeRemainder(Items.GLASS_BOTTLE)
            .food(FoodValues.DR_PEPPER),
            32, Items.GLASS_BOTTLE
    ));
    public static final Item LIQUID_GOLD = register("liquid_gold", new CompatConsumableItem(new Item.Settings().maxCount(16).recipeRemainder(Items.GLASS_BOTTLE)
            .food(FoodValues.LIQUID_GOLD),
            32 , Items.GLASS_BOTTLE
    ));
    public static final Item GOLDEN_PASTA = register("golden_pasta", new CompatConsumableItem(new Item.Settings().maxCount(16).recipeRemainder(Items.BOWL)
            .food(FoodValues.GOLDEN_PASTA),
            32 , Items.BOWL
    ));
    public static final Item FOOLS_STEW = register("fools_stew", new CompatConsumableItem(new Item.Settings().maxCount(16).recipeRemainder(Items.BOWL)
            .food(FoodValues.FOOLS_STEW),
            32 , Items.BOWL
    ));
    public static final Item GOLDEN_BURGER = register("golden_burger", new CompatConsumableItem(new Item.Settings().food(FoodValues.GOLDEN_BURGER)));

    //Hidden items used purely for rendering, I know this seems pretty unecessary
    //If I can find an easier way to render item models directly that doesn't require substituting a bunch of methods from the ItemRenderer, I will remove these
    //TODO Find a nice way to directly render item models
    public static final Item KATANA_SHEATH = register("katana_sheath", new HiddenItem());
    public static final Item IRAEDEUS_SHEATH = register("iraedeus_sheath", new HiddenItem());


    private static void addToCombatItemGroup(FabricItemGroupEntries entries) {
        entries.addAfter(Items.TRIDENT, AURADEUS);
        entries.addAfter(Items.TRIDENT, TWISTEEL_SICKLE);
        entries.addAfter(Items.TRIDENT, TWISTEEL_CHARM);
        entries.addAfter(Items.TRIDENT, GILDED_HORN);
        entries.addAfter(Items.TRIDENT, SWORDSPEAR);
        entries.addAfter(Items.TRIDENT, IRAEDEUS);
        entries.addAfter(Items.TRIDENT, KATANA);
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

    private static Item registerWithGui(String name, Item item) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            GildedGloryClient.guiModels.putIfAbsent(item, new ModelIdentifier(Identifier.of(GildedGlory.MOD_ID,"gui/" + name), "inventory"));
        }
        return Registry.register(Registries.ITEM, GildedGlory.id(name), item);
    }

    public static void registerModItems() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(ModItems::addToCombatItemGroup);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(ModItems::addToIngredientItemGroup);
    }
}
