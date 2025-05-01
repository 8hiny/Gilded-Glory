package shiny.gildedglory.common.block;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import shiny.gildedglory.common.block.entity.HeatedAnvilBlockEntity;
import shiny.gildedglory.common.registry.block.entity.ModBlockEntities;
import shiny.gildedglory.common.util.GildedGloryUtil;

public class HeatedAnvilBlock extends BlockWithEntity {

    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    private static final VoxelShape BASE_SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 4.0, 14.0);
    private static final VoxelShape X_STEP_SHAPE = Block.createCuboidShape(3.0, 4.0, 4.0, 13.0, 5.0, 12.0);
    private static final VoxelShape X_STEM_SHAPE = Block.createCuboidShape(4.0, 5.0, 6.0, 12.0, 10.0, 10.0);
    private static final VoxelShape X_FACE_SHAPE = Block.createCuboidShape(0.0, 10.0, 3.0, 16.0, 16.0, 13.0);
    private static final VoxelShape Z_STEP_SHAPE = Block.createCuboidShape(4.0, 4.0, 3.0, 12.0, 5.0, 13.0);
    private static final VoxelShape Z_STEM_SHAPE = Block.createCuboidShape(6.0, 5.0, 4.0, 10.0, 10.0, 12.0);
    private static final VoxelShape Z_FACE_SHAPE = Block.createCuboidShape(3.0, 10.0, 0.0, 13.0, 16.0, 16.0);
    private static final VoxelShape X_AXIS_SHAPE = VoxelShapes.union(BASE_SHAPE, X_STEP_SHAPE, X_STEM_SHAPE, X_FACE_SHAPE);
    private static final VoxelShape Z_AXIS_SHAPE = VoxelShapes.union(BASE_SHAPE, Z_STEP_SHAPE, Z_STEM_SHAPE, Z_FACE_SHAPE);

    public HeatedAnvilBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.HEATED_ANVIL, world.isClient() ? HeatedAnvilBlockEntity::clientTick : HeatedAnvilBlockEntity::serverTick);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);

        this.spawnParticles(world, pos, random);
        if (random.nextInt(24) == 0) {
            world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 1.0f + random.nextFloat(), random.nextFloat() * 0.7f + 0.3f, false);
        }
    }

    public void spawnParticles(World world, BlockPos pos, Random random) {
        double d = GildedGloryUtil.random(-0.5f, 0.5f);
        double e = GildedGloryUtil.random(-0.5f, 0.5f);
        double f = GildedGloryUtil.random(-0.05f, 0.05f);
        double g = GildedGloryUtil.random(-0.05f, 0.05f);
        double h = GildedGloryUtil.random(-0.05f, 0.05f);

        world.addParticle(ParticleTypes.SMOKE, pos.getX() + 0.5 + d, pos.getY() + 1, pos.getZ() + 0.5 + e, f, g, h);
        if (random.nextInt(20) == 0) world.addParticle(ParticleTypes.FLAME, pos.getX() + 0.5 + d, pos.getY() + 1, pos.getZ() + 0.5 + e, f, g, h);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().rotateYClockwise());
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new HeatedAnvilBlockEntity(pos, state);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction direction = state.get(FACING);
        return direction.getAxis() == Direction.Axis.X ? X_AXIS_SHAPE : Z_AXIS_SHAPE;
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
