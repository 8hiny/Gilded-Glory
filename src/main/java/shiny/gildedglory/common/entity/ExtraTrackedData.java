package shiny.gildedglory.common.entity;

import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.PositionSourceType;

public class ExtraTrackedData {

    public static final TrackedDataHandler<HomingTarget> TARGET = new TrackedDataHandler.ImmutableHandler<>() {
        public void write(PacketByteBuf buf, HomingTarget target) {
            if (target.hasEntity()) {
                buf.writeVarInt(target.getEntityId());
            }
            else {
                buf.writeVarInt(Integer.MIN_VALUE);
            }

            if (target.hasBlock()) {
                buf.writeBlockPos(target.getBlockPos());
            }
            else {
                buf.writeBlockPos(BlockPos.ORIGIN);
            }

            Vec3d pos = target.getPos();
            buf.writeDouble(pos.x);
            buf.writeDouble(pos.y);
            buf.writeDouble(pos.z);
        }
        public HomingTarget read(PacketByteBuf buf) {
            int id = buf.readVarInt();
            BlockPos pos = buf.readBlockPos();
            HomingTarget target = new HomingTarget(buf.readDouble(), buf.readDouble(), buf.readDouble());

            if (id != Integer.MIN_VALUE) {
                target.setEntityId(id);
            }
            if (pos != BlockPos.ORIGIN) {
                target = new HomingTarget(pos);
            }
            return target;
        }
    };

    public static final TrackedDataHandler<PositionSource> POSITION_SOURCE = new TrackedDataHandler.ImmutableHandler<>() {
        public void write(PacketByteBuf buf, PositionSource source) {
            PositionSourceType.write(source, buf);
        }
        public PositionSource read(PacketByteBuf buf) {
            return PositionSourceType.read(buf);
        }
    };

    static {
        TrackedDataHandlerRegistry.register(TARGET);
        TrackedDataHandlerRegistry.register(POSITION_SOURCE);
    }
}
