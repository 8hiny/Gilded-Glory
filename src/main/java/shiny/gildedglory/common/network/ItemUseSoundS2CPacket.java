package shiny.gildedglory.common.network;

import me.pepperbell.simplenetworking.S2CPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import shiny.gildedglory.client.old.ItemUseSoundInstance;
import shiny.gildedglory.client.sound.DynamicSoundInstance;
import shiny.gildedglory.common.util.DynamicSoundManager;
import shiny.gildedglory.common.util.DynamicSoundSource;
import shiny.gildedglory.client.sound.DynamicSounds;

import java.util.UUID;

public class ItemUseSoundS2CPacket implements S2CPacket {

    public static void receiveOld(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        UUID uuid = buf.readUuid();
        ItemUseSoundInstance.Sounds sound = buf.readEnumConstant(ItemUseSoundInstance.Sounds.class);

        if (client.world != null && client.world.getPlayerByUuid(uuid) != null) {
            PlayerEntity player = client.world.getPlayerByUuid(uuid);
            client.getSoundManager().play(sound.get(player));
        }
    }

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        UUID uuid = buf.readUuid();
        Identifier id = buf.readIdentifier();

        if (client.world != null && client.world.getPlayerByUuid(uuid) != null) {
            PlayerEntity player = client.world.getPlayerByUuid(uuid);

            DynamicSoundInstance sound = DynamicSounds.get(id, (DynamicSoundSource) player);
            if (sound != null) {
                DynamicSoundManager.getInstance().play(sound);
            }
        }
    }

    private final int id;
    private final Identifier sound;

    public ItemUseSoundS2CPacket(int id, Identifier sound) {
        this.id = id;
        this.sound = sound;
    }

    public ItemUseSoundS2CPacket(PacketByteBuf buf) {
        this(buf.readInt(), buf.readIdentifier());
    }

    @Override
    public void handle(MinecraftClient client, ClientPlayNetworkHandler listener, PacketSender responseSender, SimpleChannel channel) {
        if (client.world != null && client.world.getEntityById(this.id) != null) {
            Entity entity = client.world.getEntityById(this.id);

            DynamicSoundInstance sound = DynamicSounds.get(this.sound, (DynamicSoundSource) entity);
            if (sound != null) {
                DynamicSoundManager.getInstance().play(sound);
            }
        }
    }

    @Override
    public void encode(PacketByteBuf buf) {
        buf.writeInt(this.id);
        buf.writeIdentifier(this.sound);
    }
}
