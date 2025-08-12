package shiny.gildedglory.common.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.Vec3d;
import shiny.gildedglory.GildedGlory;
import shiny.gildedglory.client.slashed_area.SlashedAreaManager;

public record SlashedAreaPayload(Vec3d center, float radius, float width, int amount, int duration) implements CustomPayload {

    public static final Id<SlashedAreaPayload> ID = new Id<>(GildedGlory.id("slashed_area"));
    public static final PacketCodec<PacketByteBuf, SlashedAreaPayload> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.codec(Vec3d.CODEC),
            SlashedAreaPayload::center,
            PacketCodecs.FLOAT,
            SlashedAreaPayload::radius,
            PacketCodecs.FLOAT,
            SlashedAreaPayload::width,
            PacketCodecs.INTEGER,
            SlashedAreaPayload::amount,
            PacketCodecs.INTEGER,
            SlashedAreaPayload::duration,
            SlashedAreaPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static class Handler implements ClientPlayNetworking.PlayPayloadHandler<SlashedAreaPayload> {
        @Override
        public void receive(SlashedAreaPayload payload, ClientPlayNetworking.Context context) {
            SlashedAreaManager.getInstance().add(payload.center, payload.radius, payload.width, payload.amount, payload.duration);
        }
    }
}
