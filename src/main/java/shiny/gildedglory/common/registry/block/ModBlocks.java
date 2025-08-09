package shiny.gildedglory.common.registry.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import shiny.gildedglory.GildedGlory;
import shiny.gildedglory.common.block.FramedChestBlock;
import shiny.gildedglory.common.block.HeatedAnvilBlock;

public class ModBlocks {

    public static final Block TWISTEEL_BLOCK = registerBlock("twisteel_block", new Block(AbstractBlock.Settings.create()
            .mapColor(MapColor.DARK_CRIMSON)
            .sounds(BlockSoundGroup.NETHERITE)
            .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
            .strength(5.0f, 6.0f)
            .requiresTool()
            .pistonBehavior(PistonBehavior.PUSH_ONLY)),
            true
    );

    public static final Block FOOLS_GOLD_BLOCK = registerBlock("fools_gold_block", new Block(AbstractBlock.Settings.create()
            .mapColor(MapColor.STONE_GRAY)
            .sounds(BlockSoundGroup.METAL)
            .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
            .strength(5.0f, 6.0f)
            .requiresTool()),
            true
    );

    public static final Block FRAMED_CHEST = registerBlock("framed_chest", new FramedChestBlock(AbstractBlock.Settings.create()
            .mapColor(MapColor.OAK_TAN)
            .sounds(BlockSoundGroup.WOOD)
            .instrument(NoteBlockInstrument.BASS)
            .strength(2.5f)
            .burnable()),
            true
    );

    public static final Block HEATED_ANVIL = registerBlock("heated_anvil", new HeatedAnvilBlock(AbstractBlock.Settings.create()
            .mapColor(MapColor.BRIGHT_RED)
            .sounds(BlockSoundGroup.ANVIL)
            .strength(5.0f, 1200.0f)
            .sounds(BlockSoundGroup.ANVIL)
            .pistonBehavior(PistonBehavior.BLOCK)
            .luminance(state -> 10)),
            false
    );

    private static Block registerBlock(String name, Block block, boolean bl) {
        if (bl) registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, GildedGlory.id(name), block);
    }

    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(Registries.ITEM, GildedGlory.id(name),
                new BlockItem(block, new Item.Settings()));
    }

    public static void registerModBlocks() {
    }
}
