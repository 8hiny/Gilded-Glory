package shiny.gildedglory.common.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.Vec3d;
import shiny.gildedglory.GildedGlory;
import shiny.gildedglory.client.test.TestManager;

public record TestRenderPayload(Vec3d position, boolean clear) implements CustomPayload {

    public static final Id<TestRenderPayload> ID = new Id<>(GildedGlory.id("test_render"));
    public static final PacketCodec<PacketByteBuf, TestRenderPayload> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.codec(Vec3d.CODEC),
            TestRenderPayload::position,
            PacketCodecs.BOOL,
            TestRenderPayload::clear,
            TestRenderPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static class Handler implements ClientPlayNetworking.PlayPayloadHandler<TestRenderPayload> {
        @Override
        public void receive(TestRenderPayload payload, ClientPlayNetworking.Context context) {
            if (payload.clear) {
                TestManager.getInstance().clear();
            }
            else {
                TestManager.getInstance().addObject(TestManager.Type.SPHERE, payload.position);
            }
        }
    }
}
