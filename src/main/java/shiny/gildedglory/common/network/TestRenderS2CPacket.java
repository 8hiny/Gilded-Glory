package shiny.gildedglory.common.network;

import me.pepperbell.simplenetworking.S2CPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;
import shiny.gildedglory.client.test.TestManager;

public class TestRenderS2CPacket implements S2CPacket {

    private final Vec3d pos;
    private final boolean clear;

    public TestRenderS2CPacket(Vec3d pos, boolean clear) {
        this.clear = clear;
        this.pos = pos;
    }

    public TestRenderS2CPacket(PacketByteBuf buf) {
        this(new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble()), buf.readBoolean());
    }

    @Override
    public void handle(MinecraftClient client, ClientPlayNetworkHandler listener, PacketSender responseSender, SimpleChannel channel) {
        if (this.clear) {
            TestManager.getInstance().clear();
        }
        else {
            TestManager.getInstance().addObject(TestManager.Type.SPHERE, this.pos);
        }
    }

    @Override
    public void encode(PacketByteBuf buf) {
        buf.writeDouble(this.pos.x);
        buf.writeDouble(this.pos.y);
        buf.writeDouble(this.pos.z);
        buf.writeBoolean(this.clear);
    }
}
