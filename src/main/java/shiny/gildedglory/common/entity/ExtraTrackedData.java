package shiny.gildedglory.common.entity;

import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;

public class ExtraTrackedData {

    public static final TrackedDataHandler<HomingTarget> TARGET = new TrackedDataHandler.ImmutableHandler<>() {
        public void write(PacketByteBuf buf, HomingTarget target) {
            if (target.hasEntity()) {
                buf.writeVarInt(target.getEntityId());
            }
            else {
                buf.writeVarInt(Integer.MIN_VALUE);
            }

            Vec3d pos = target.pos();
            buf.writeDouble(pos.x);
            buf.writeDouble(pos.y);
            buf.writeDouble(pos.z);
        }
        public HomingTarget read(PacketByteBuf buf) {
            int id = buf.readVarInt();
            HomingTarget target = new HomingTarget(buf.readDouble(), buf.readDouble(), buf.readDouble());

            if (id != Integer.MIN_VALUE) {
                target.setEntityId(id);
            }
            return target;
        }
    };

    static {
        TrackedDataHandlerRegistry.register(TARGET);
    }
}
