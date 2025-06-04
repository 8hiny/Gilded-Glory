package shiny.gildedglory.client.pose;

import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.LivingEntity;

import java.util.function.BiConsumer;

public class CustomArmPose implements ArmPose {

    private BiConsumer<ModelPart, Context> leftArmTransform;
    private BiConsumer<ModelPart, Context>rightArmTransform;
    private final boolean twoHanded;
    private final boolean preventLimbSwing;

    public CustomArmPose(BiConsumer<ModelPart, Context> transform, boolean left, boolean preventLimbSwing) {
        if (left) this.leftArmTransform = transform;
        else this.rightArmTransform = transform;
        this.twoHanded = false;
        this.preventLimbSwing = preventLimbSwing;
    }

    public CustomArmPose(BiConsumer<ModelPart, Context> leftArmTransform, BiConsumer<ModelPart, Context> rightArmTransform, boolean preventLimbSwing) {
        this.leftArmTransform = leftArmTransform;
        this.rightArmTransform = rightArmTransform;
        twoHanded = true;
        this.preventLimbSwing = preventLimbSwing;
    }

    public void transformLeft(ModelPart leftArm, Context ctx) {
        leftArmTransform.accept(leftArm, ctx);
    }

    public void transformRight(ModelPart rightArm, Context ctx) {
        rightArmTransform.accept(rightArm, ctx);
    }

    public boolean preventLimbSwing() {
        return this.preventLimbSwing;
    }

    @Override
    public boolean twoHanded() {
        return this.twoHanded;
    }

    @Override
    public Value value() {
        return Value.CUSTOM;
    }

    public record Context(LivingEntity entity, float headPitch, float headYaw, float bodyPitch, float bodyYaw, boolean leftHanded) {
    }
}
