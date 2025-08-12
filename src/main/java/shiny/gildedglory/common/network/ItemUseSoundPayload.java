package shiny.gildedglory.common.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import shiny.gildedglory.GildedGlory;
import shiny.gildedglory.client.sound.DynamicSoundInstance;
import shiny.gildedglory.client.sound.DynamicSounds;
import shiny.gildedglory.common.util.DynamicSoundManager;

public record ItemUseSoundPayload(int id, Identifier sound) implements CustomPayload {

    public static final Id<ItemUseSoundPayload> ID = new Id<>(GildedGlory.id("item_use_sound"));
    public static final PacketCodec<PacketByteBuf, ItemUseSoundPayload> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER,
            ItemUseSoundPayload::id,
            Identifier.PACKET_CODEC,
            ItemUseSoundPayload::sound,
            ItemUseSoundPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static class Handler implements ClientPlayNetworking.PlayPayloadHandler<ItemUseSoundPayload> {
        @Override
        public void receive(ItemUseSoundPayload payload, ClientPlayNetworking.Context context) {
            MinecraftClient client = context.client();
            if (client.world != null && client.world.getEntityById(payload.id) != null) {
                Entity entity = client.world.getEntityById(payload.id);

                if (entity != null) {
                    DynamicSoundInstance sound = DynamicSounds.get(payload.sound, entity);
                    if (sound != null) {
                        DynamicSoundManager.getInstance().play(sound);
                    }
                }
            }
        }
    }
}
