package shiny.gildedglory.mixin;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import shiny.gildedglory.common.block.HeatedAnvilBlock;
import shiny.gildedglory.common.registry.block.ModBlocks;

@Mixin(value = AnvilBlock.class, priority = 999)
public abstract class AnvilBlockMixin extends FallingBlock {

    public AnvilBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "onUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;openHandledScreen(Lnet/minecraft/screen/NamedScreenHandlerFactory;)Ljava/util/OptionalInt;"), cancellable = true)
    private void gildedglory$setHeated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack stack = player.getStackInHand(hand);

        if (stack.isOf(Items.BLAZE_POWDER)) {
            BlockState heatedState = ModBlocks.HEATED_ANVIL.getDefaultState().with(HeatedAnvilBlock.FACING, state.get(AnvilBlock.FACING));
            world.setBlockState(pos, heatedState);

            world.playSound(null, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS);
            world.playSound(null, pos, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS);

            stack.decrement(1);
            cir.setReturnValue(ActionResult.SUCCESS);
        }
    }
}
