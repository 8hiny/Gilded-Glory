package shiny.gildedglory.mixin.sound;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import shiny.gildedglory.common.util.DynamicSoundSource;

@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin implements DynamicSoundSource {

    @Shadow public abstract BlockPos getPos();
    @Shadow public abstract boolean isRemoved();

    @Override
    public Vec3d getPosition() {
        return this.getPos().toCenterPos();
    }

    @Override
    public boolean canPlay() {
        return !this.isRemoved();
    }
}
