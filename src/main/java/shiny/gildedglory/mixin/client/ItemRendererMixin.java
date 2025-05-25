package shiny.gildedglory.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import shiny.gildedglory.GildedGloryClient;
import shiny.gildedglory.common.registry.item.ModItems;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {

    @Shadow @Final private MinecraftClient client;

    @ModifyVariable(method = "renderItem", at = @At(value = "HEAD"), argsOnly = true)
    public BakedModel gildedglory$useGuiModel(BakedModel value, ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (renderMode == ModelTransformationMode.GUI || renderMode == ModelTransformationMode.GROUND) {

            if (stack.isOf(ModItems.AURADEUS)) {
                value = ((ItemRendererAccessor) this).gildedglory$getModels().getModelManager().getModel(GildedGloryClient.AURADEUS_GUI);
            }
            if (stack.isOf(ModItems.SWORDSPEAR)) {
                value = ((ItemRendererAccessor) this).gildedglory$getModels().getModelManager().getModel(GildedGloryClient.SWORDSPEAR_GUI);
            }
            if (stack.isOf(ModItems.IRAEDEUS)) {
                value = ((ItemRendererAccessor) this).gildedglory$getModels().getModelManager().getModel(GildedGloryClient.IRAEDEUS_GUI);
            }
            if (stack.isOf(ModItems.KATANA)) {
                value = ((ItemRendererAccessor) this).gildedglory$getModels().getModelManager().getModel(GildedGloryClient.KATANA_GUI);
            }
            if (stack.isOf(ModItems.DR_PEPPER)) {
                value = ((ItemRendererAccessor) this).gildedglory$getModels().getModelManager().getModel(GildedGloryClient.DR_PEPPER_GUI);
            }

            if (this.client.world != null && this.client.player != null) {
                value = value.getOverrides().apply(value, stack, client.world, this.client.player, 0);
            }
        }
        return value;
    }
}