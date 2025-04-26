package shiny.gildedglory.common.block;

import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.block.enums.ChestType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.*;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;
import shiny.gildedglory.common.block.entity.FramedChestBlockEntity;
import shiny.gildedglory.common.registry.block.entity.ModBlockEntities;

import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;

public class FramedChestBlock extends BlockWithEntity implements Waterloggable {

    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final EnumProperty<ChestType> CHEST_TYPE = Properties.CHEST_TYPE;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    protected static final VoxelShape DOUBLE_NORTH_SHAPE = Block.createCuboidShape(1.0, 0.0, 0.0, 15.0, 14.0, 15.0);
    protected static final VoxelShape DOUBLE_SOUTH_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 14.0, 16.0);
    protected static final VoxelShape DOUBLE_WEST_SHAPE = Block.createCuboidShape(0.0, 0.0, 1.0, 15.0, 14.0, 15.0);
    protected static final VoxelShape DOUBLE_EAST_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 16.0, 14.0, 15.0);
    protected static final VoxelShape SINGLE_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 14.0, 15.0);

    private static final FramedChestBlockInventoryRetriever INVENTORY_RETRIEVER = new FramedChestBlockInventoryRetriever();
    private static final FramedChestBlockNameRetriever NAME_RETRIEVER = new FramedChestBlockNameRetriever();

    public FramedChestBlock(Settings settings) {
        super(settings);

        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(CHEST_TYPE, ChestType.SINGLE)
                .with(WATERLOGGED, false)
        );
    }

    public static DoubleBlockProperties.Type getDoubleBlockType(BlockState state) {
        ChestType chestType = state.get(CHEST_TYPE);
        if (chestType == ChestType.SINGLE) {
            return DoubleBlockProperties.Type.SINGLE;
        }
        else {
            return chestType == ChestType.RIGHT ? DoubleBlockProperties.Type.FIRST : DoubleBlockProperties.Type.SECOND;
        }
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {

        if (state.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        if (neighborState.isOf(this) && direction.getAxis().isHorizontal()) {
            ChestType chestType = neighborState.get(CHEST_TYPE);
            if (state.get(CHEST_TYPE) == ChestType.SINGLE
                    && chestType != ChestType.SINGLE
                    && state.get(FACING) == neighborState.get(FACING)
                    && getFacing(neighborState) == direction.getOpposite()) {
                return state.with(CHEST_TYPE, chestType.getOpposite());
            }
        }
        else if (getFacing(state) == direction) {
            return state.with(CHEST_TYPE, ChestType.SINGLE);
        }

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (state.get(CHEST_TYPE) == ChestType.SINGLE) {
            return SINGLE_SHAPE;
        }
        else {
            switch (getFacing(state)) {
                case NORTH:
                default:
                    return DOUBLE_NORTH_SHAPE;
                case SOUTH:
                    return DOUBLE_SOUTH_SHAPE;
                case WEST:
                    return DOUBLE_WEST_SHAPE;
                case EAST:
                    return DOUBLE_EAST_SHAPE;
            }
        }
    }

    public static Direction getFacing(BlockState state) {
        Direction direction = state.get(FACING);
        return state.get(CHEST_TYPE) == ChestType.LEFT ? direction.rotateYClockwise() : direction.rotateYCounterclockwise();
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        ChestType chestType = ChestType.SINGLE;
        Direction direction = ctx.getHorizontalPlayerFacing().getOpposite();
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        boolean bl = ctx.shouldCancelInteraction();

        Direction direction2 = ctx.getSide();
        if (direction2.getAxis().isHorizontal() && bl) {

            Direction direction3 = this.getNeighborChestDirection(ctx, direction2.getOpposite());
            if (direction3 != null && direction3.getAxis() != direction2.getAxis()) {
                direction = direction3;
                chestType = direction3.rotateYCounterclockwise() == direction2.getOpposite() ? ChestType.RIGHT : ChestType.LEFT;
            }
        }

        if (chestType == ChestType.SINGLE && !bl) {

            if (direction == this.getNeighborChestDirection(ctx, direction.rotateYClockwise())) {
                chestType = ChestType.LEFT;
            }
            else if (direction == this.getNeighborChestDirection(ctx, direction.rotateYCounterclockwise())) {
                chestType = ChestType.RIGHT;
            }
        }

        return this.getDefaultState().with(FACING, direction).with(CHEST_TYPE, chestType).with(WATERLOGGED, Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Nullable
    private Direction getNeighborChestDirection(ItemPlacementContext ctx, Direction dir) {
        BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos().offset(dir));
        return blockState.isOf(this) && blockState.get(CHEST_TYPE) == ChestType.SINGLE ? blockState.get(FACING) : null;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (itemStack.hasCustomName()) {

            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof FramedChestBlockEntity) {
                ((FramedChestBlockEntity)blockEntity).setCustomName(itemStack.getName());
            }
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {

            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof Inventory) {
                ItemScatterer.spawn(world, pos, (Inventory)blockEntity);
                world.updateComparators(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient()) {
            return ActionResult.SUCCESS;
        }
        else {
            NamedScreenHandlerFactory namedScreenHandlerFactory = this.createScreenHandlerFactory(state, world, pos);
            if (namedScreenHandlerFactory != null) {
                player.openHandledScreen(namedScreenHandlerFactory);
                player.incrementStat(this.getOpenStat());
                PiglinBrain.onGuardedBlockInteracted(player, true);
            }
            return ActionResult.CONSUME;
        }
    }

    protected Stat<Identifier> getOpenStat() {
        return Stats.CUSTOM.getOrCreateStat(Stats.OPEN_CHEST);
    }

    public BlockEntityType<? extends FramedChestBlockEntity> getExpectedEntityType() {
        return ModBlockEntities.FRAMED_CHEST;
    }

    @Nullable
    public static Inventory getInventory(FramedChestBlock block, BlockState state, World world, BlockPos pos, boolean ignoreBlocked) {
        return block.getBlockEntitySource(state, world, pos, ignoreBlocked).apply(INVENTORY_RETRIEVER).orElse(null);
    }

    public DoubleBlockProperties.PropertySource<? extends FramedChestBlockEntity> getBlockEntitySource(BlockState state, World world, BlockPos pos, boolean ignoreBlocked) {
        BiPredicate<WorldAccess, BlockPos> biPredicate;
        if (ignoreBlocked) {
            biPredicate = (worldx, posx) -> false;
        }
        else {
            biPredicate = FramedChestBlock::isChestBlocked;
        }
        return DoubleBlockProperties.toPropertySource(ModBlockEntities.FRAMED_CHEST, FramedChestBlock::getDoubleBlockType, FramedChestBlock::getFacing, FACING, state, world, pos, biPredicate);
    }

    @Nullable
    @Override
    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        return this.getBlockEntitySource(state, world, pos, false).apply(NAME_RETRIEVER).orElse(null);
    }

    public static DoubleBlockProperties.PropertyRetriever<FramedChestBlockEntity, Float2FloatFunction> getAnimationProgressRetriever(LidOpenable progress) {
        return new DoubleBlockProperties.PropertyRetriever<>() {

            public Float2FloatFunction getFromBoth(FramedChestBlockEntity framedChestBlockEntity1, FramedChestBlockEntity framedChestBlockEntity2) {
                return tickDelta -> Math.max(framedChestBlockEntity1.getAnimationProgress(tickDelta), framedChestBlockEntity2.getAnimationProgress(tickDelta));
            }

            public Float2FloatFunction getFrom(FramedChestBlockEntity framedChestBlockEntity) {
                return framedChestBlockEntity::getAnimationProgress;
            }

            public Float2FloatFunction getFallback() {
                return progress::getAnimationProgress;
            }
        };
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new FramedChestBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ? checkType(type, this.getExpectedEntityType(), FramedChestBlockEntity::clientTick) : null;
    }

    public static boolean isChestBlocked(WorldAccess world, BlockPos pos) {
        return hasBlockOnTop(world, pos) || hasCatOnTop(world, pos);
    }

    private static boolean hasBlockOnTop(BlockView world, BlockPos pos) {
        BlockPos blockPos = pos.up();
        return world.getBlockState(blockPos).isSolidBlock(world, blockPos);
    }

    private static boolean hasCatOnTop(WorldAccess world, BlockPos pos) {
        List<CatEntity> list = world.getNonSpectatingEntities(
                CatEntity.class,
                new Box(pos.getX(), (pos.getY() + 1), pos.getZ(), (pos.getX() + 1), (pos.getY() + 2), (pos.getZ() + 1))
        );
        if (!list.isEmpty()) {
            for (CatEntity catEntity : list) {
                if (catEntity.isInSittingPose()) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput(getInventory(this, state, world, pos, false));
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, CHEST_TYPE, WATERLOGGED);
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof FramedChestBlockEntity) {
            ((FramedChestBlockEntity) blockEntity).onScheduledTick();
        }
    }


    public static class FramedChestBlockInventoryRetriever implements DoubleBlockProperties.PropertyRetriever<FramedChestBlockEntity, Optional<Inventory>> {

        public Optional<Inventory> getFromBoth(FramedChestBlockEntity framedChestBlockEntity1, FramedChestBlockEntity framedChestBlockEntity2) {
            return Optional.of(new DoubleInventory(framedChestBlockEntity1, framedChestBlockEntity2));
        }

        public Optional<Inventory> getFrom(FramedChestBlockEntity framedChestBlockEntity) {
            return Optional.of(framedChestBlockEntity);
        }

        public Optional<Inventory> getFallback() {
            return Optional.empty();
        }
    };

    public static class FramedChestBlockNameRetriever implements DoubleBlockProperties.PropertyRetriever<FramedChestBlockEntity, Optional<NamedScreenHandlerFactory>> {

        public Optional<NamedScreenHandlerFactory> getFromBoth(FramedChestBlockEntity framedChestBlockEntity, FramedChestBlockEntity framedChestBlockEntity2) {
            final Inventory inventory = new DoubleInventory(framedChestBlockEntity, framedChestBlockEntity2);
            return Optional.of(new NamedScreenHandlerFactory() {
                @Nullable
                @Override
                public ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                    if (framedChestBlockEntity.checkUnlocked(playerEntity) && framedChestBlockEntity2.checkUnlocked(playerEntity)) {
                        framedChestBlockEntity.checkLootInteraction(playerInventory.player);
                        framedChestBlockEntity2.checkLootInteraction(playerInventory.player);
                        return GenericContainerScreenHandler.createGeneric9x6(i, playerInventory, inventory);
                    }
                    else {
                        return null;
                    }
                }

                @Override
                public Text getDisplayName() {
                    if (framedChestBlockEntity.hasCustomName()) {
                        return framedChestBlockEntity.getDisplayName();
                    }
                    else {
                        return (framedChestBlockEntity2.hasCustomName() ? framedChestBlockEntity2.getDisplayName() : Text.translatable("container.gildedglory.framed_chest_double"));
                    }
                }
            });
        }

        public Optional<NamedScreenHandlerFactory> getFrom(FramedChestBlockEntity framedChestBlockEntity) {
            return Optional.of(framedChestBlockEntity);
        }

        public Optional<NamedScreenHandlerFactory> getFallback() {
            return Optional.empty();
        }
    };
}