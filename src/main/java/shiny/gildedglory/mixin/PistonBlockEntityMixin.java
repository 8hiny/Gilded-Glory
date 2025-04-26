package shiny.gildedglory.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shiny.gildedglory.common.registry.block.ModBlocks;
import shiny.gildedglory.common.util.HeatedAnvilRecipeHandler;

@Mixin(PistonBlockEntity.class)
public abstract class PistonBlockEntityMixin {

    @Unique private static DefaultedList<ItemEntity> ingredients = DefaultedList.of();

    @WrapWithCondition(method = "pushEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/PistonBlockEntity;moveEntity(Lnet/minecraft/util/math/Direction;Lnet/minecraft/entity/Entity;DLnet/minecraft/util/math/Direction;)V"))
    private static boolean gildedglory$getIntersectingItems(Direction direction, Entity entity, double distance, Direction movementDirection) {
        if (entity instanceof ItemEntity item) {
            ingredients.add(item);
            return false;
        }
        return true;
    }

    @Inject(method = "pushEntities", at = @At(value = "TAIL"))
    private static void gildedglory$applyCompressionRecipes(World world, BlockPos pos, float f, PistonBlockEntity blockEntity, CallbackInfo ci) {
        if (blockEntity.getFacing() == Direction.DOWN && world.getBlockState(new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ())).getBlock().equals(ModBlocks.HEATED_ANVIL)) {
            HeatedAnvilRecipeHandler.compress(ingredients, world, pos);
        }
        ingredients.clear();
    }
}
