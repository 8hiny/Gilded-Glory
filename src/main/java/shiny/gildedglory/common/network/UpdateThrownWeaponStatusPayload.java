package shiny.gildedglory.common.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import shiny.gildedglory.GildedGlory;
import shiny.gildedglory.common.registry.component.ModComponents;

public record UpdateThrownWeaponStatusPayload(boolean status, boolean returningOrTargeting) implements CustomPayload {

    public static final Id<UpdateThrownWeaponStatusPayload> ID = new Id<>(GildedGlory.id("update_thrown_weapon_status"));
    public static final PacketCodec<PacketByteBuf, UpdateThrownWeaponStatusPayload> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.BOOL,
            UpdateThrownWeaponStatusPayload::status,
            PacketCodecs.BOOL,
            UpdateThrownWeaponStatusPayload::returningOrTargeting,
            UpdateThrownWeaponStatusPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static class Handler implements ServerPlayNetworking.PlayPayloadHandler<UpdateThrownWeaponStatusPayload> {
        @Override
        public void receive(UpdateThrownWeaponStatusPayload payload, ServerPlayNetworking.Context context) {
            ModComponents.THROWABLE_WIP.maybeGet(context.player()).ifPresent(component -> {
                if (payload.returningOrTargeting) component.returning = payload.status;
                else component.targeting = payload.status;
            });
        }
    }
}
