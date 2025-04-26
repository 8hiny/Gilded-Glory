package shiny.gildedglory.mixin.sound;

import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import shiny.gildedglory.common.util.DynamicSoundSource;

@Mixin(Entity.class)
public abstract class EntityMixin implements DynamicSoundSource {

    @Shadow public abstract Vec3d getPos();
    @Shadow public abstract boolean isAlive();
    @Shadow public abstract Text getName();

    @Override
    public Vec3d getPosition() {
        return this.getPos();
    }

    @Override
    public boolean canPlay() {
        return this.isAlive();
    }

    @Override
    public float getStress() {
        return DynamicSoundSource.super.getStress();
    }
}
