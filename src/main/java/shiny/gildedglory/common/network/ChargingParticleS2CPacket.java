package shiny.gildedglory.common.network;

import me.pepperbell.simplenetworking.S2CPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import org.joml.Vector3f;
import shiny.gildedglory.client.particle.effect.ColoredParticleEffect;
import shiny.gildedglory.common.registry.particle.ModParticles;
import shiny.gildedglory.common.util.GildedGloryUtil;

public class ChargingParticleS2CPacket implements S2CPacket {

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        int id = buf.readInt();

        float r = buf.readFloat();
        float g = buf.readFloat();
        float b = buf.readFloat();

        float x = buf.readFloat();
        float y = buf.readFloat();
        float z = buf.readFloat();

        float dx = buf.readFloat();
        float dy = buf.readFloat();
        float dz = buf.readFloat();

        if (client.player != null && client.world != null) {
            ClientPlayerEntity player = client.player;
            Entity entity = client.world.getEntityById(id);

            if (entity != null && (!client.options.getPerspective().isFirstPerson() || player != entity) && !entity.isInvisibleTo(player)) {
                ColoredParticleEffect particle = new ColoredParticleEffect(ModParticles.SQUARE, new Vector3f(r, g, b), GildedGloryUtil.random(0.2f, 0.5f));
                client.particleManager.addParticle(particle, x, y, z, dx, dy, dz);
            }
        }
    }

    private static <T extends ParticleEffect> T readParticleParameters(PacketByteBuf buf, ParticleType<T> type) {
        return type.getParametersFactory().read(type, buf);
    }

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
                ColoredParticleEffect particle = new ColoredParticleEffect(ModParticles.SQUARE, new Vector3f(this.red, this.green, this.blue), GildedGloryUtil.random(0.2f, 0.5f));
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
