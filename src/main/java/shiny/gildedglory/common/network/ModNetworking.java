package shiny.gildedglory.common.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class ModNetworking {

    public static void registerModPayloads() {
        //S2C
        PayloadTypeRegistry.playS2C().register(ChargingParticlePayload.ID, ChargingParticlePayload.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(ItemUseSoundPayload.ID, ItemUseSoundPayload.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(SlashedAreaPayload.ID, SlashedAreaPayload.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(TestRenderPayload.ID, TestRenderPayload.PACKET_CODEC);

        //C2S
        PayloadTypeRegistry.playC2S().register(UpdateIraedeusStatusPayload.ID, UpdateIraedeusStatusPayload.PACKET_CODEC);
    }

    public static void registerModServerReceivers() {
        ServerPlayNetworking.registerGlobalReceiver(UpdateIraedeusStatusPayload.ID, new UpdateIraedeusStatusPayload.Handler());
    }

    public static void registerModClientReceivers() {
        ClientPlayNetworking.registerGlobalReceiver(ChargingParticlePayload.ID, new ChargingParticlePayload.Handler());
        ClientPlayNetworking.registerGlobalReceiver(ItemUseSoundPayload.ID, new ItemUseSoundPayload.Handler());
        ClientPlayNetworking.registerGlobalReceiver(SlashedAreaPayload.ID, new SlashedAreaPayload.Handler());
        ClientPlayNetworking.registerGlobalReceiver(TestRenderPayload.ID, new TestRenderPayload.Handler());
    }
}
