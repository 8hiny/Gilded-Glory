package shiny.gildedglory.common.network;

import me.pepperbell.simplenetworking.S2CPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import shiny.gildedglory.GildedGlory;
import shiny.gildedglory.common.entity.HomingTarget;
import shiny.gildedglory.common.entity.IraedeusEntity;

public class IraedeusTargetS2CPacket implements S2CPacket {

    private final int entityId;
    private final HomingTarget target;

    public IraedeusTargetS2CPacket(int entityId, HomingTarget target) {
        this.entityId = entityId;
        this.target = target;
    }

    public IraedeusTargetS2CPacket(PacketByteBuf buf) {
        int entityId = buf.readVarInt();
        int targetId = buf.readVarInt();

        HomingTarget target = new HomingTarget(buf.readDouble(), buf.readDouble(), buf.readDouble());;
        if (targetId != Integer.MIN_VALUE) {
            ClientWorld world = MinecraftClient.getInstance().world;
            if (world != null) {
                Entity entity = world.getEntityById(targetId);
                if (entity != null) {
                    GildedGlory.LOGGER.info("Found target entity when sending packet with homing target!");
                    target = new HomingTarget(entity);
                }
            }
        }
        this.entityId = entityId;
        this.target = target;
    }

    @Override
    public void handle(MinecraftClient client, ClientPlayNetworkHandler listener, PacketSender responseSender, SimpleChannel channel) {
        ClientWorld world = client.world;
        if (world != null && world.getEntityById(this.entityId) instanceof IraedeusEntity entity) {
            entity.setTarget(this.target);
            entity.setTargeting(true);
        }
    }

    @Override
    public void encode(PacketByteBuf buf) {
        buf.writeVarInt(this.entityId);

        int targetId;
        if (this.target.hasEntity()) {
            targetId = this.target.getEntity().getId();
        }
        else {
            targetId = Integer.MIN_VALUE;
        }
        buf.writeVarInt(targetId);

        buf.writeDouble(this.target.pos().x);
        buf.writeDouble(this.target.pos().y);
        buf.writeDouble(this.target.pos().z);
    }
}
