package shiny.gildedglory.common.network;

import me.pepperbell.simplenetworking.S2CPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import org.joml.Vector3f;
import shiny.gildedglory.client.particle.effect.VectorParticleEffect;
import shiny.gildedglory.common.registry.particle.ModParticles;
import shiny.gildedglory.common.util.GildedGloryUtil;

public class ChargingParticleS2CPacket implements S2CPacket {

    private final int id;
    private final float red;
    private final float green;
    private final float blue;
    private final float x;
    private final float y;
    private final float z;
    private final float dx;
    private final float dy;
    private final float dz;

    public ChargingParticleS2CPacket(int id, float red, float green, float blue, float x, float y, float z, float dx, float dy, float dz) {
        this.id = id;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.x = x;
        this.y = y;
        this.z = z;
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
    }

    public ChargingParticleS2CPacket(PacketByteBuf buf) {
        this(buf.readInt(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat()
        );
    }

    @Override
    public void handle(MinecraftClient client, ClientPlayNetworkHandler listener, PacketSender responseSender, SimpleChannel channel) {
        if (client.player != null && client.world != null) {
            ClientPlayerEntity player = client.player;
            Entity entity = client.world.getEntityById(this.id);

            if (entity != null && (!client.options.getPerspective().isFirstPerson() || player != entity) && !entity.isInvisibleTo(player)) {
                VectorParticleEffect particle = new VectorParticleEffect(ModParticles.SQUARE, new Vector3f(this.red, this.green, this.blue), GildedGloryUtil.random(0.2f, 0.5f), 0);
                client.particleManager.addParticle(particle, this.x, this.y, this.z, this.dx, this.dy, this.dz);
            }
        }
    }

    @Override
    public void encode(PacketByteBuf buf) {
        buf.writeInt(this.id);
        buf.writeFloat(this.red);
        buf.writeFloat(this.green);
        buf.writeFloat(this.blue);
        buf.writeFloat(this.x);
        buf.writeFloat(this.y);
        buf.writeFloat(this.z);
        buf.writeFloat(this.dx);
        buf.writeFloat(this.dy);
        buf.writeFloat(this.dz);
    }
}
