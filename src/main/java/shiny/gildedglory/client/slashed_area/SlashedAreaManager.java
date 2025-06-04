package shiny.gildedglory.client.slashed_area;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import shiny.gildedglory.client.render.ModShaders;
import shiny.gildedglory.client.render.ModRenderLayers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SlashedAreaManager {

    private static SlashedAreaManager instance;
    private final List<SlashedArea> areas = new ArrayList<>();

    private SlashedAreaManager() {
    }

    public static SlashedAreaManager getInstance() {
        if (instance == null) {
            instance = new SlashedAreaManager();
        }
        return instance;
    }

    public void tick() {
        this.areas.forEach(SlashedArea::tick);
    }

    public static void renderTick(WorldRenderContext ctx) {
        SlashedAreaManager manager = getInstance();
        MatrixStack matrices = ctx.matrixStack();
        VertexConsumerProvider vertexConsumers = ctx.consumers();

        Vec3d camPos = ctx.camera().getPos();

        if (vertexConsumers != null) {
            manager.render(matrices, vertexConsumers, camPos);
        }
    }

    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Vec3d cameraPos) {
        matrices.push();
        matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        if (!this.areas.isEmpty()) {
            for (Iterator<SlashedArea> iterator = this.areas.iterator(); iterator.hasNext();) {
                SlashedArea area = iterator.next();
                if (!area.isDone()) {
                    List<Vec3d> points = area.getVertices();
                    float width = area.getWidth();
                    float alpha = MathHelper.clamp(1.2f - area.normalizedAge(), 0.0f, 1.0f);
                    int count = area.age() + 1 <= area.getAmount() - 1 ? (area.age() + 1) * 2 : points.size() - 1;

                    //Slash quads
                    matrices.push();
                    VertexConsumer slash = vertexConsumers.getBuffer(ModRenderLayers.getSlashedArea());
                    for (int i = 0; i < count; i++) {
                        Vec3d point = points.get(i);
                        Vec3d point1 = points.get(i + 1);

                        Vec3d direction = point1.subtract(point);
                        Vec3d midPoint = point.add(direction.multiply(0.5));

                        Vec3d normal = cameraPos.subtract(point).crossProduct(direction).normalize();

                        Vec3d mid = midPoint.add(normal.multiply(width));
                        Vec3d mid1 = midPoint.subtract(normal.multiply(width));

                        vertex(matrices, slash, point, alpha);
                        vertex(matrices, slash, mid, alpha * 0.8f);
                        vertex(matrices, slash, point1, alpha);
                        vertex(matrices, slash, mid1, alpha * 0.8f);
                    }
                    matrices.pop();

                    //Mirror quads between slashes
                    matrices.push();
                    VertexConsumer mirror = vertexConsumers.getBuffer(ModRenderLayers.getMirror());
                    for (int i = 0; i < count - 2; i++) {
                        Vec3d a = points.get(i);
                        Vec3d b = points.get(i + 1);
                        Vec3d c = points.get(i + 2);
                        Vec3d d = points.get(i + 3);

                        vertex(matrices, mirror, a);
                        vertex(matrices, mirror, b);
                        vertex(matrices, mirror, d);
                        vertex(matrices, mirror, c);
                    }
                    matrices.pop();
                }
                else {
                    iterator.remove();
                }
            }
        }
        matrices.pop();
    }

    public void vertex(MatrixStack matrices, VertexConsumer vertexConsumer, Vec3d point) {
        MatrixStack.Entry entry = matrices.peek();
        vertexConsumer.vertex(entry.getPositionMatrix(), (float) point.x, (float) point.y, (float) point.z)
                .next();
    }

    public void vertex(MatrixStack matrices, VertexConsumer vertexConsumer, Vec3d point, float alpha) {
        MatrixStack.Entry entry = matrices.peek();
        vertexConsumer.vertex(entry.getPositionMatrix(), (float) point.x, (float) point.y, (float) point.z)
                .color(1.0f, 1.0f, 1.0f, alpha)
                .next();
    }

    public void add(Vec3d center, float radius, float width, int amount, int duration) {
        areas.add(new SlashedArea(center, radius, width, amount, duration));
    }

    public boolean isEmpty() {
        return this.areas.isEmpty();
    }
}
