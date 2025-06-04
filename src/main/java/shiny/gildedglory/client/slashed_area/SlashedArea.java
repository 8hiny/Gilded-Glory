package shiny.gildedglory.client.slashed_area;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import shiny.gildedglory.GildedGlory;
import shiny.gildedglory.common.util.GildedGloryUtil;

import java.util.ArrayList;
import java.util.List;

public class SlashedArea {

    private final List<Vec3d> vertices = new ArrayList<>();
    private final float maxWidth;
    private float width;
    private final float amount;
    private final int duration;
    private int age;
    private boolean isDone;

    public SlashedArea(Vec3d center, float radius, float width, int amount, int duration) {
        for (int i = 0; i < amount; i++) {
            double d = GildedGloryUtil.random(-0.6, 0.6) * radius;
            double e = GildedGloryUtil.random(-0.6, 0.6) * radius;
            double f = GildedGloryUtil.random(-0.6, 0.6) * radius;
            double g = GildedGloryUtil.random(-0.6, 0.6) * radius;
            double h = GildedGloryUtil.random(-0.6, 0.6) * radius;
            double j = GildedGloryUtil.random(-0.6, 0.6) * radius;

            Vec3d point = center.add(d, e, f);
            Vec3d point1 = center.add(g, h, j);

            //Space points out to prevent short lines
            Vec3d direction = point1.subtract(point);
            if (direction.lengthSquared() < 20.0) {
                direction = direction.normalize().multiply(2);
                point = point.subtract(direction);
                point1 = point1.add(direction);
            }

            this.vertices.add(point);
            this.vertices.add(point1);
        }

        this.maxWidth = width;
        this.width = width;
        this.amount = amount;
        this.duration = duration;
        this.age = 0;
        this.isDone = false;
    }

    public List<Vec3d> getVertices() {
        return this.vertices;
    }

    public float getWidth() {
        return this.width;
    }

    public float getAmount() {
        return this.amount;
    }

    public int age() {
        return this.age;
    }

    public float normalizedAge() {
        return (float) this.age / this.duration;
    }

    public void tick() {
        if (!this.isDone) {
            this.width = MathHelper.lerp(this.normalizedAge(), this.maxWidth, 0);
            if (this.width < 0.0f) this.width = 0.0f;
        }

        if (this.age < this.duration + this.amount) {
            this.age++;
        }
        else {
            this.setDone();
        }
    }

    public void setDone() {
        this.isDone = true;
    }

    public boolean isDone() {
        return this.isDone;
    }
}
