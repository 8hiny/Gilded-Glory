package shiny.gildedglory.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Represents a target location; Returns a position as a Vec3d.
 * If instantiated using an Entity, that entity should be updated in order to update its position.
 */
public class HomingTarget {

    private Entity entity;
    private int entityId = Integer.MIN_VALUE;
    private Vec3d pos;
    private BlockPos blockPos;
    public static HomingTarget EMPTY = new HomingTarget(Vec3d.ZERO);

    public HomingTarget(Vec3d pos) {
        this.pos = pos;
    }

    public HomingTarget(double x, double y, double z) {
        this.pos = new Vec3d(x, y ,z);
    }

    public HomingTarget(Entity target) {
        this.entity = target;
        this.entityId = target.getId();
    }

    public HomingTarget(BlockPos pos) {
        this.blockPos = pos;
    }

    public Vec3d getPos() {
        if (this.hasEntity()) {
            return this.entity.getPos().add(0, this.entity.getHeight() / 2, 0);
        }
        if (this.hasBlock()) {
            return this.blockPos.toCenterPos();
        }
        return this.pos;
    }

    public boolean hasEntity() {
        return this.entity != null;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public void setEntityId(int id) {
        this.entityId = id;
    }

    public boolean hasBlock() {
        return this.blockPos != null;
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    /**
     * Updates the entity for this target. Necessary when syncing targets to the client, for example through the DataTracker.
     * For entities which can store a HomingTarget, should be called in Entity#onTrackedDataSet().
     * @see IraedeusEntity#onTrackedDataSet(TrackedData)
     */
    public void updateEntity(World world) {
        if (this.entityId != Integer.MIN_VALUE) {
            Entity entity = world.getEntityById(this.entityId);
            if (entity != null) {
                this.entity = entity;
            }
        }
    }
}
