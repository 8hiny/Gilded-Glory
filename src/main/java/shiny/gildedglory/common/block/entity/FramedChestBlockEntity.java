package shiny.gildedglory.common.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import shiny.gildedglory.common.registry.block.entity.ModBlockEntities;

public class FramedChestBlockEntity extends ChestBlockEntity {

    public FramedChestBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.FRAMED_CHEST, blockPos, blockState);
    }

    @Override
    protected Text getContainerName() {
        return Text.translatable("container.gildedglory.framed_chest");
    }
}
