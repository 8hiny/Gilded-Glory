package shiny.gildedglory.common.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import shiny.gildedglory.GildedGlory;
import shiny.gildedglory.common.registry.component.ModComponents;

public record UpdateIraedeusStatusPayload(boolean status, boolean returningOrTargeting) implements CustomPayload {

    public static final Id<UpdateIraedeusStatusPayload> ID = new Id<>(GildedGlory.id("update_iraedeus_status"));
    public static final PacketCodec<PacketByteBuf, UpdateIraedeusStatusPayload> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.BOOL,
            UpdateIraedeusStatusPayload::status,
            PacketCodecs.BOOL,
            UpdateIraedeusStatusPayload::returningOrTargeting,
            UpdateIraedeusStatusPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static class Handler implements ServerPlayNetworking.PlayPayloadHandler<UpdateIraedeusStatusPayload> {
        @Override
        public void receive(UpdateIraedeusStatusPayload payload, ServerPlayNetworking.Context context) {
            ModComponents.IRAEDEUS.maybeGet(context.player()).ifPresent(component -> {
                if (payload.returningOrTargeting) component.returning = payload.status;
                else component.targeting = payload.status;
            });
        }
    }
}
