package shiny.gildedglory.common.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;
import shiny.gildedglory.GildedGlory;
import shiny.gildedglory.client.particle.effect.TestParticleEffect;
import shiny.gildedglory.common.registry.particle.ModParticles;
import shiny.gildedglory.common.util.GildedGloryUtil;

public record ChargingParticlePayload(int id, Vec3d position, Vec3d delta, Vector3f color) implements CustomPayload {

    public static final Id<ChargingParticlePayload> ID = new Id<>(GildedGlory.id("charging_particle"));
    public static final PacketCodec<PacketByteBuf, ChargingParticlePayload> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER,
            ChargingParticlePayload::id,
            PacketCodecs.codec(Vec3d.CODEC),
            ChargingParticlePayload::position,
            PacketCodecs.codec(Vec3d.CODEC),
            ChargingParticlePayload::delta,
            PacketCodecs.VECTOR3F,
            ChargingParticlePayload::color,
            ChargingParticlePayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static class Handler implements ClientPlayNetworking.PlayPayloadHandler<ChargingParticlePayload> {
        @Override
        public void receive(ChargingParticlePayload payload, ClientPlayNetworking.Context context) {
            MinecraftClient client = context.client();
            ClientPlayerEntity player = context.player();
            World world = player.getWorld();
            Entity entity = world.getEntityById(payload.id);

            if (entity != null && (!client.options.getPerspective().isFirstPerson() || player != entity) && !entity.isInvisibleTo(player)) {
                Vec3d pos = payload.position;
                Vec3d delta = payload.delta;

                TestParticleEffect particle = new TestParticleEffect(ModParticles.SQUARE, payload.color, GildedGloryUtil.random(0.2f, 0.5f), 40);
                client.particleManager.addParticle(particle, pos.x, pos.y, pos.z, delta.x, delta.y, delta.z);
            }
        }
    }
}
