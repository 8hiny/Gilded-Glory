package shiny.gildedglory.common.network;

import me.pepperbell.simplenetworking.S2CPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;
import shiny.gildedglory.client.slashed_area.SlashedAreaManager;

public class SlashedAreaS2CPacket implements S2CPacket {

    private final Vec3d center;
    private final float radius;
    private final float width;
    private final int amount;
    private final int duration;

    public SlashedAreaS2CPacket(Vec3d center, float radius, float width, int amount, int duration) {
        this.center = center;
        this.radius = radius;
        this.width = width;
        this.amount = amount;
        this.duration = duration;
    }

    public SlashedAreaS2CPacket(PacketByteBuf buf) {
        this(new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble()), buf.readFloat(), buf.readFloat(), buf.readInt(), buf.readInt());
    }

    @Override
    public void handle(MinecraftClient client, ClientPlayNetworkHandler listener, PacketSender responseSender, SimpleChannel channel) {
        SlashedAreaManager.getInstance().add(this.center, this.radius, this.width, this.amount, this.duration);
    }

    @Override
    public void encode(PacketByteBuf buf) {
        buf.writeDouble(this.center.x);
        buf.writeDouble(this.center.y);
        buf.writeDouble(this.center.z);

        buf.writeFloat(this.radius);
        buf.writeFloat(this.width);
        buf.writeInt(this.amount);
        buf.writeInt(this.duration);
    }
}
