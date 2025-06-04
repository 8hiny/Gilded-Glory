package shiny.gildedglory.mixin.client;

import net.minecraft.client.render.entity.model.BipedEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import shiny.gildedglory.client.pose.ArmPose;

@Mixin(BipedEntityModel.ArmPose.class)
public abstract class BipedArmPoseMixin implements ArmPose {

    @Shadow public abstract boolean isTwoHanded();

    @Override
    public boolean twoHanded() {
        return this.isTwoHanded();
    }

    @Override
    public Value value() {
        return Value.VANILLA;
    }
}
