package shiny.gildedglory.common.registry.block.entity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import shiny.gildedglory.GildedGlory;
import shiny.gildedglory.common.block.entity.FramedChestBlockEntity;
import shiny.gildedglory.common.block.entity.HeatedAnvilBlockEntity;
import shiny.gildedglory.common.registry.block.ModBlocks;

public class ModBlockEntities {

    public static final BlockEntityType<FramedChestBlockEntity> FRAMED_CHEST = Registry.register(Registries.BLOCK_ENTITY_TYPE, GildedGlory.id("framed_chest"),
            FabricBlockEntityTypeBuilder.create(FramedChestBlockEntity::new, ModBlocks.FRAMED_CHEST).build()
    );
    public static final BlockEntityType<HeatedAnvilBlockEntity> HEATED_ANVIL = Registry.register(Registries.BLOCK_ENTITY_TYPE, GildedGlory.id("heated_anvil"),
            FabricBlockEntityTypeBuilder.create(HeatedAnvilBlockEntity::new, ModBlocks.HEATED_ANVIL).build()
    );

    public static void registerModBlockEntities() {
    }
}
