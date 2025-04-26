package shiny.gildedglory.client.render;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;
import shiny.gildedglory.common.component.entity.IraedeusComponent;
import shiny.gildedglory.common.registry.component.ModComponents;

public class SummonedIraedeusFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {

    private final ItemRenderer renderer;

    public SummonedIraedeusFeatureRenderer(FeatureRendererContext<T, M> context, ItemRenderer renderer) {
        super(context);
        this.renderer = renderer;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, LivingEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (entity instanceof PlayerEntity player) {
            IraedeusComponent component = ModComponents.IRAEDEUS.get(player);

            if (component.isSummoned()) {
                ItemStack stack = ModComponents.IRAEDEUS.get(player).getStack();

                matrices.push();

                matrices.translate(0.0f, 0.0f, 1.0f);
                matrices.scale(1.25f, 1.25f, 1.25f);
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-45.0f));

                this.renderer
                        .renderItem(
                                stack, ModelTransformationMode.FIXED, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, entity.getWorld(), entity.getId()
                        );
                matrices.pop();
            }
        }
    }
}
