package shiny.gildedglory.common.network;

import me.pepperbell.simplenetworking.C2SPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import shiny.gildedglory.common.registry.component.ModComponents;

public class UpdateTargetingC2SPacket implements C2SPacket {

    private final boolean targeting;

    public UpdateTargetingC2SPacket(boolean targeting) {
        this.targeting = targeting;
    }

    public UpdateTargetingC2SPacket(PacketByteBuf buf) {
        this(buf.readBoolean());
    }

    @Override
    public void handle(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler listener, PacketSender responseSender, SimpleChannel channel) {
        ModComponents.IRAEDEUS.maybeGet(player).ifPresent(component -> {
            component.targeting = this.targeting;
        });
    }

    @Override
    public void encode(PacketByteBuf buf) {
        buf.writeBoolean(this.targeting);
    }
}
