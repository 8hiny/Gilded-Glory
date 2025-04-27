package shiny.gildedglory.client.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import shiny.gildedglory.common.component.entity.IraedeusComponent;
import shiny.gildedglory.common.registry.component.ModComponents;

public class SummonedIraedeusFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {

    private final ItemRenderer renderer;
    private Vec3d velocity = Vec3d.ZERO;
    private Vec3d lastPos = Vec3d.ZERO;
    private Vec3d lastDifference = Vec3d.ZERO;
    private double lastLength = 0.0;

    public SummonedIraedeusFeatureRenderer(FeatureRendererContext<T, M> context, ItemRenderer renderer) {
        super(context);
        this.renderer = renderer;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, LivingEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (entity instanceof AbstractClientPlayerEntity player) {
            IraedeusComponent component = ModComponents.IRAEDEUS.get(player);

            if (component.isSummoned()) {
                ItemStack stack = ModComponents.IRAEDEUS.get(player).getStack();

                //Y value for idle hovering
                float y = MathHelper.sin((player.age + tickDelta) * 0.05f) * 0.15f;

                //The speed at which the player is moving; is used to offset backwards
                double length = MathHelper.clamp(player.getVelocity().horizontalLength() * 2.0, 0.0, 1.0);
                length = MathHelper.lerp(0.03f + 0.01f * tickDelta, this.lastLength, length);


                matrices.push();

                matrices.translate(0.0f, y, 1.0f + length);
                if (player.isInSneakingPose()) matrices.translate(0.0f, -0.135f, 0.0f);

                matrices.multiply(RotationAxis.POSITIVE_X.rotation((float) length * 0.5f));
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-45.0f));

                matrices.scale(1.25f, 1.25f, 1.25f);

                Matrix4f matrix = matrices.peek().getPositionMatrix();
                Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
                Vec3d currentPos = transformToWorld(matrix, camera);

                this.renderer
                        .renderItem(
                                stack, ModelTransformationMode.FIXED, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, entity.getWorld(), entity.getId()
                        );
                matrices.pop();

                this.lastLength = length;
            }
        }
    }

    public void testRender(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, LivingEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (entity instanceof AbstractClientPlayerEntity player) {
            IraedeusComponent component = ModComponents.IRAEDEUS.get(player);

            if (component.isSummoned()) {
                ItemStack stack = ModComponents.IRAEDEUS.get(player).getStack();

                float y = MathHelper.sin((player.age + tickDelta) * 0.05f) * 0.15f;

                double length = MathHelper.clamp(player.getVelocity().horizontalLength() * 2.0, 0.0, 1.0);
                length = MathHelper.lerp(0.03f + 0.01f * tickDelta, this.lastLength, length);


                matrices.push();
                matrices.translate(0.0f, y, 1.0f + length);
                if (player.isInSneakingPose()) matrices.translate(0.0f, -0.135f, 0.0f);

                matrices.multiply(RotationAxis.POSITIVE_X.rotation((float) length * 0.5f));
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-45.0f));

                matrices.scale(1.25f, 1.25f, 1.25f);

                Matrix4f matrix = matrices.peek().getPositionMatrix();
                Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
                Vec3d currentPos = transformToWorld(matrix, camera);
                if(this.lastPos.lengthSquared() <= 0.01) this.lastPos = currentPos;

                Vec3d offset = currentPos.subtract(this.lastPos);

                var maxSpeed = 0.5;
                Vec3d springForce = calcSpringForce(offset, this.velocity, 1.0, 2.0);
                this.velocity = this.velocity.add(springForce.multiply(0.5));

                if (this.velocity.lengthSquared() > maxSpeed * maxSpeed) {
                    this.velocity.normalize().multiply(maxSpeed);
                }

                currentPos = currentPos.add(velocity);
                matrices.translate(this.velocity.x, this.velocity.y, this.velocity.z);

                this.renderer
                        .renderItem(
                                stack, ModelTransformationMode.FIXED, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, entity.getWorld(), entity.getId()
                        );
                matrices.pop();

                this.lastLength = length;
                this.lastPos = currentPos;
            }
        }
    }

    public Vec3d calcSpringForce(Vec3d offset, Vec3d vel, double strength, double damping) {
        return offset.multiply(strength).subtract(vel.multiply(damping));
    }

    private Vec3d transformToWorld(Matrix4f matrix, Camera camera) {
        // Convert (0,0,0) in local item space to transformed coordinates
        Vector4f localPos = new Vector4f(0, 0, 0, 1);
        matrix.transform(localPos);

        // Convert view space to world space by adding the camera position
        Vec3d cameraPos = camera.getPos();
        return new Vec3d(cameraPos.x + localPos.x(), cameraPos.y + localPos.y(), cameraPos.z + localPos.z());
    }
}
