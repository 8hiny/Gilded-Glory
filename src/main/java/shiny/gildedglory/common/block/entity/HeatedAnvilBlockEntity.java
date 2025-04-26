package shiny.gildedglory.common.block.entity;

import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import shiny.gildedglory.common.util.DynamicSoundSource;
import shiny.gildedglory.common.block.HeatedAnvilBlock;
import shiny.gildedglory.common.registry.block.entity.ModBlockEntities;

public class HeatedAnvilBlockEntity extends BlockEntity implements DynamicSoundSource {

    private int age;

    public HeatedAnvilBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HEATED_ANVIL, pos, state);
    }

    public static void clientTick(World world, BlockPos pos, BlockState state, HeatedAnvilBlockEntity entity) {
        if (entity.age < 3600) entity.age++;
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, HeatedAnvilBlockEntity entity) {
        if (entity.age < 3600) {
            entity.age++;
        }
        else if (entity.age == 3600) {
            BlockState anvilState = Blocks.ANVIL.getDefaultState().with(AnvilBlock.FACING, state.get(HeatedAnvilBlock.FACING));
            world.setBlockState(pos, anvilState);
            world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS);
        }
    }

    @Override
    public Vec3d getPosition() {
        return this.getPos().toCenterPos();
    }

    @Override
    public boolean canPlay() {
        return !this.isRemoved();
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("age", this.age);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.age = nbt.getInt("age");
    }
}
