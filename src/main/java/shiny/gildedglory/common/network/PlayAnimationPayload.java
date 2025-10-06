package shiny.gildedglory.common.network;

import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractFadeModifier;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.core.util.Ease;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import shiny.gildedglory.GildedGlory;
import shiny.gildedglory.common.util.AnimatablePlayer;

public record PlayAnimationPayload(int id, Identifier animation, boolean play) implements CustomPayload {

    public static final Id<PlayAnimationPayload> ID = new Id<>(GildedGlory.id("play_animation"));
    public static final PacketCodec<PacketByteBuf, PlayAnimationPayload> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER,
            PlayAnimationPayload::id,
            Identifier.PACKET_CODEC,
            PlayAnimationPayload::animation,
            PacketCodecs.BOOL,
            PlayAnimationPayload::play,
            PlayAnimationPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static class Handler implements ClientPlayNetworking.PlayPayloadHandler<PlayAnimationPayload> {
        @Override
        public void receive(PlayAnimationPayload payload, ClientPlayNetworking.Context context) {
            MinecraftClient client = context.client();
            if (client.world != null) {
                Entity entity = client.world.getEntityById(payload.id);

                if (entity instanceof AnimatablePlayer animatablePlayer) {
                    if (payload.play) {
                        KeyframeAnimation animation = (KeyframeAnimation) PlayerAnimationRegistry.getAnimation(payload.animation);
                        if (animation != null) {
                            animatablePlayer.gildedglory$getModifierLayer().setAnimation(new KeyframeAnimationPlayer(animation));
                        }
                    }
                    else {
                        animatablePlayer.gildedglory$getModifierLayer().replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(5, Ease.INOUTSINE), null);
                    }
                }
            }
        }
    }
}
