package shiny.gildedglory.client.test;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import shiny.gildedglory.client.render.shape.SphereRenderer;

import java.util.ArrayList;
import java.util.List;

public class TestManager {

    public static TestManager instance;
    private final List<TestRenderingObject> objects = new ArrayList<>();

    public static TestManager getInstance() {
        if (instance == null) {
            instance = new TestManager();
        }
        return instance;
    }

    public static void tick(WorldRenderContext ctx) {
        TestManager manager = getInstance();
        MatrixStack matrices = ctx.matrixStack();
        VertexConsumerProvider vertexConsumerProvider = ctx.consumers();

        if (vertexConsumerProvider != null) {
            for (TestRenderingObject object : manager.objects) {
                Vec3d pos = object.pos;
                Vec3d camPos = ctx.camera().getPos();
                pos = new Vec3d(pos.x - camPos.x, pos.y - camPos.y, pos.z - camPos.z);

                if (object.type == Type.SPHERE) {
                    SphereRenderer.render(matrices, vertexConsumerProvider, pos, 15);
                }
            }
        }
    }

    public void addObject(Type type, Vec3d pos) {
        TestRenderingObject object = new TestRenderingObject(type, pos);

        this.objects.add(object);
    }

    public void clear() {
        this.objects.clear();
    }

    public record TestRenderingObject(Type type, Vec3d pos) {
    }

    public enum Type {
        CUBE,
        SPHERE,
        CONE
    }
}
