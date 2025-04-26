package shiny.gildedglory.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class HomingTarget {

    private Entity entity;
    private Vec3d pos;

    public HomingTarget(Vec3d pos) {
        this.pos = pos;
    }

    public HomingTarget(double x, double y, double z) {
        this.pos = new Vec3d(x, y ,z);
    }

    public HomingTarget(Entity target) {
        this.entity = target;
    }

    public HomingTarget(BlockPos pos) {
        this.pos = pos.toCenterPos();
    }

    public Vec3d pos() {
        if (this.hasEntity()) {
            return this.entity.getPos().add(0, this.entity.getHeight() / 2, 0);
        }
        return this.pos;
    }

    public boolean hasEntity() {
        return this.entity != null;
    }

    public Entity getEntity() {
        return this.entity;
    }
}
