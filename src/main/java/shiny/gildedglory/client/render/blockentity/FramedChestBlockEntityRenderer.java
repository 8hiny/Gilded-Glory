package shiny.gildedglory.client.render.blockentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.LightmapCoordinatesRetriever;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;
import shiny.gildedglory.GildedGlory;
import shiny.gildedglory.common.block.FramedChestBlock;
import shiny.gildedglory.common.registry.block.ModBlocks;
import shiny.gildedglory.common.block.entity.FramedChestBlockEntity;

public class FramedChestBlockEntityRenderer implements BlockEntityRenderer<FramedChestBlockEntity> {

    public static final EntityModelLayer SINGLE_MODEL_LAYER = new EntityModelLayer(GildedGlory.id("framed_chest_single"), "main");
    public static final EntityModelLayer DOUBLE_MODEL_LAYER = new EntityModelLayer(GildedGlory.id("framed_chest_double"), "main");
    public static final Identifier SINGLE_TEXTURE = GildedGlory.id("textures/entity/framed_chest/single.png");
    public static final Identifier DOUBLE_TEXTURE = GildedGlory.id("textures/entity/framed_chest/double.png");
    private final ModelPart singleChestLid;
    private final ModelPart doubleChestLid;
    private final ModelPart singleChestBase;
    private final ModelPart doubleChestBase;
    private static final FramedChestBlockEntity RENDER_ITEM = new FramedChestBlockEntity(BlockPos.ORIGIN, ModBlocks.FRAMED_CHEST.getDefaultState());

    public FramedChestBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        ModelPart singleRoot = ctx.getLayerModelPart(SINGLE_MODEL_LAYER);
        this.singleChestLid = singleRoot.getChild("lid");
        this.singleChestBase = singleRoot.getChild("base");

        ModelPart doubleRoot = ctx.getLayerModelPart(DOUBLE_MODEL_LAYER);
        this.doubleChestLid = doubleRoot.getChild("lid");
        this.doubleChestBase = doubleRoot.getChild("base");

    }

    public static TexturedModelData getSingleTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        modelPartData.addChild("base", ModelPartBuilder.create().uv(0, 19).cuboid(1.0f, 0.0f, 1.0f, 14.0f, 10.0f, 14.0f), ModelTransform.NONE);
        modelPartData.addChild("lid", ModelPartBuilder.create()
                        .uv(0, 0).cuboid(1.0f, 0.0f, 0.0f, 14.0f, 5.0f, 14.0f)
                        .uv(0, 0).cuboid(6.5f, -1.0f, 14.0f, 3.0f, 3.0f, 1.0f),
                ModelTransform.pivot(0.0f, 9.0f, 1.0f)
        );

        return TexturedModelData.of(modelData, 64, 64);
    }

    public static TexturedModelData getDoubleTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        modelPartData.addChild("base", ModelPartBuilder.create().uv(0, 19).cuboid(1.0f, 0.0f, 1.0f, 30.0f, 10.0f, 14.0f), ModelTransform.NONE);
        modelPartData.addChild("lid", ModelPartBuilder.create()
                        .uv(0, 0).cuboid(1.0f, 0.0f, 0.0f, 30.0f, 5.0f, 14.0f)
                        .uv(0, 0).cuboid(14.0f, -2.0f, 14.0f, 4.0f, 4.0f, 1.0f),
                ModelTransform.pivot(0.0f, 9.0f, 1.0f)
        );

        return TexturedModelData.of(modelData, 128, 128);
    }

    public void render(FramedChestBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        World world = entity.getWorld();

        BlockState blockState = world != null ? entity.getCachedState() : ModBlocks.FRAMED_CHEST.getDefaultState().with(FramedChestBlock.FACING, Direction.SOUTH);
        ChestType chestType = blockState.contains((Property) FramedChestBlock.CHEST_TYPE) ? blockState.get(FramedChestBlock.CHEST_TYPE) : ChestType.SINGLE;

        if (chestType.equals(ChestType.LEFT)) return;
        boolean single = chestType.equals(ChestType.SINGLE);

        if (blockState.getBlock() instanceof FramedChestBlock framedChestBlock) {
            matrices.push();

            float f = (blockState.get(FramedChestBlock.FACING)).asRotation();

            matrices.translate(0.5f, 0.5f, 0.5f);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-f));
            matrices.translate(-0.5f, -0.5f, -0.5f);

            DoubleBlockProperties.PropertySource<? extends FramedChestBlockEntity> propertySource;
            if (world != null) propertySource = framedChestBlock.getBlockEntitySource(blockState, world, entity.getPos(), true);
            else propertySource = DoubleBlockProperties.PropertyRetriever::getFallback;

            float openFactor = 1.0f - propertySource.apply(FramedChestBlock.getAnimationProgressRetriever(entity)).get(tickDelta);
            openFactor = 1.0F - openFactor * openFactor * openFactor;

            light = propertySource.apply(new LightmapCoordinatesRetriever<>()).applyAsInt(light);

            ModelPart lid = single ? this.singleChestLid : this.doubleChestLid;
            ModelPart base = single ? this.singleChestBase : this.doubleChestBase;

            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(single ? SINGLE_TEXTURE : DOUBLE_TEXTURE));
            this.render(matrices, vertexConsumer, lid, base, openFactor, light, overlay);

            matrices.pop();
        }
    }

    private void render(MatrixStack matrices, VertexConsumer vertices, ModelPart lid, ModelPart base, float openFactor, int light, int overlay) {
        lid.pitch = -(openFactor * (float) (Math.PI / 2));
        lid.render(matrices, vertices, light, overlay);
        base.render(matrices, vertices, light, overlay);
    }

    public static void renderItem(MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        MinecraftClient.getInstance().getBlockEntityRenderDispatcher().renderEntity(RENDER_ITEM, matrixStack, vertexConsumers, light, overlay);
    }
}