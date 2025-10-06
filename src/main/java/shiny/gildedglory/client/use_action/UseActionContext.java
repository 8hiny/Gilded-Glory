package shiny.gildedglory.client.use_action;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public record UseActionContext(
        HeldItemRenderer renderer,
        AbstractClientPlayerEntity player,
        float tickDelta,
        float pitch,
        int light,
        Hand hand,
        float swingProgress,
        boolean leftHanded,
        ItemStack stack,
        MatrixStack matrices,
        VertexConsumerProvider vertexConsumers
) {
}
