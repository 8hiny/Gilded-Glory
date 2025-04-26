package shiny.gildedglory.common.entity;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;
import shiny.gildedglory.GildedGlory;

public class ExtraTrackedData {

    public static final TrackedDataHandler<HomingTarget> TARGET = new TrackedDataHandler.ImmutableHandler<>() {

        public void write(PacketByteBuf buf, HomingTarget target) {
            if (target.hasEntity()) {
                buf.writeVarInt(target.getEntity().getId());
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

            if (id != Integer.MIN_VALUE) {
                MinecraftClient client = MinecraftClient.getInstance();
                if (client.world != null) {
                    Entity entity = client.world.getEntityById(id);
                    if (entity != null) {
                        GildedGlory.LOGGER.info("Target sent from server had entity stored! Retrieving that entity...");
                        return new HomingTarget(entity);
                    }
                }
            }
            return new HomingTarget(buf.readDouble(), buf.readDouble(), buf.readDouble());
        }
    };

    static {
        TrackedDataHandlerRegistry.register(TARGET);
    }
}
