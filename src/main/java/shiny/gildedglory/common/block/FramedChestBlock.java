package shiny.gildedglory.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.TrappedChestBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;
import shiny.gildedglory.common.block.entity.FramedChestBlockEntity;
import shiny.gildedglory.common.registry.block.entity.ModBlockEntities;

import java.util.Optional;
import java.util.function.BiPredicate;

public class FramedChestBlock extends ChestBlock {

    public FramedChestBlock(AbstractBlock.Settings settings) {
        super(settings, () -> ModBlockEntities.FRAMED_CHEST);
    }

    @Override
    public MapCodec<FramedChestBlock> getCodec() {
        return createCodec(FramedChestBlock::new);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new FramedChestBlockEntity(pos, state);
    }

    @Override
    protected boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return MathHelper.clamp(ChestBlockEntity.getPlayersLookingInChestCount(world, pos), 0, 15);
    }

    @Override
    protected int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return direction == Direction.UP ? state.getWeakRedstonePower(world, pos, direction) : 0;
    }

    private static final DoubleBlockProperties.PropertyRetriever<FramedChestBlockEntity, Optional<NamedScreenHandlerFactory>> NAME_RETRIEVER = new DoubleBlockProperties.PropertyRetriever<>() {
        public Optional<NamedScreenHandlerFactory> getFromBoth(FramedChestBlockEntity framedChestEntity, FramedChestBlockEntity framedChestEntity2) {
            final Inventory inventory = new DoubleInventory(framedChestEntity, framedChestEntity2);
            return Optional.of(new NamedScreenHandlerFactory() {

                @Nullable
                @Override
                public ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                    if (framedChestEntity.checkUnlocked(playerEntity) && framedChestEntity2.checkUnlocked(playerEntity)) {
                        framedChestEntity.generateLoot(playerInventory.player);
                        framedChestEntity2.generateLoot(playerInventory.player);
                        return GenericContainerScreenHandler.createGeneric9x6(i, playerInventory, inventory);
                    } else {
                        return null;
                    }
                }

                @Override
                public Text getDisplayName() {
                    if (framedChestEntity.hasCustomName()) {
                        return framedChestEntity.getDisplayName();
                    } else {
                        return framedChestEntity2.hasCustomName() ? framedChestEntity2.getDisplayName() : Text.translatable("container.gildedglory.framed_chest_double");
                    }
                }
            });
        }

        public Optional<NamedScreenHandlerFactory> getFrom(FramedChestBlockEntity framedChestBlock) {
            return Optional.of(framedChestBlock);
        }

        public Optional<NamedScreenHandlerFactory> getFallback() {
            return Optional.empty();
        }
    };

    @Nullable
    @Override
    protected NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        return this.getBlockEntitySource(state, world, pos, false).apply(NAME_RETRIEVER).orElse(null);
    }

    @Override
    public DoubleBlockProperties.PropertySource<FramedChestBlockEntity> getBlockEntitySource(
            BlockState state, World world, BlockPos pos, boolean ignoreBlocked
    ) {
        BiPredicate<WorldAccess, BlockPos> biPredicate;
        if (ignoreBlocked) {
            biPredicate = (worldx, posx) -> false;
        }
        else {
            biPredicate = ChestBlock::isChestBlocked;
        }

        return DoubleBlockProperties.toPropertySource(
                (BlockEntityType<FramedChestBlockEntity>) this.entityTypeRetriever.get(),
                ChestBlock::getDoubleBlockType,
                ChestBlock::getFacing,
                FACING,
                state,
                world,
                pos,
                biPredicate
        );
    }
}
