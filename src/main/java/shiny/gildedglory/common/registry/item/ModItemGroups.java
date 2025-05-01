package shiny.gildedglory.common.registry.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import shiny.gildedglory.GildedGlory;
import shiny.gildedglory.common.registry.block.ModBlocks;

public class ModItemGroups {

    public static final ItemGroup GILDED_GLORY_GROUP = Registry.register(Registries.ITEM_GROUP, GildedGlory.id("gilded_glory_group"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.gildedglory"))
                    .icon(() -> new ItemStack(ModItems.AURADEUS)).entries((displayContext, entries) -> {
                        entries.add(ModItems.TWISTEEL_INGOT);
                        entries.add(ModBlocks.TWISTEEL_BLOCK.asItem());
                        entries.add(ModItems.AURADEUS);
                        entries.add(ModItems.TWISTEEL_SICKLE);
                        entries.add(ModItems.TWISTEEL_CHARM);
                        entries.add(ModItems.FOOLS_GOLD_INGOT);
                        entries.add(ModBlocks.FOOLS_GOLD_BLOCK.asItem());
                        entries.add(ModItems.FOOLS_GOLD_HELMET);
                        entries.add(ModItems.FOOLS_GOLD_CHESTPLATE);
                        entries.add(ModItems.FOOLS_GOLD_LEGGINGS);
                        entries.add(ModItems.FOOLS_GOLD_BOOTS);
                        entries.add(ModItems.GILDED_HORN);
                        entries.add(ModItems.SWORDSPEAR);
                        entries.add(ModBlocks.FRAMED_CHEST.asItem());
                        entries.add(ModItems.LIQUID_GOLD);
                        entries.add(ModItems.FOOLS_STEW);
                        entries.add(ModItems.GOLDEN_BURGER);
                        entries.add(ModItems.GOLDEN_PASTA);
                        entries.add(ModItems.DR_PEPPER);
                        entries.add(ModItems.GLOOMETAL_INGOT);
                        entries.add(ModItems.IRAEDEUS);

            }).build());

    public static void registerModItemGroups() {
    }
}
