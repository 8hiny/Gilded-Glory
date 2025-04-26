package shiny.gildedglory.common.network;

import io.github.fabricators_of_create.porting_lib.util.EnvExecutor;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.fabricmc.api.EnvType;
import shiny.gildedglory.GildedGlory;

public class ModPackets {

    public static final SimpleChannel GILDED_GLORY_CHANNEL = new SimpleChannel(GildedGlory.id("main"));

    public static void registerModPackets() {
        GILDED_GLORY_CHANNEL.initServerListener();
        EnvExecutor.runWhenOn(EnvType.CLIENT, () -> GILDED_GLORY_CHANNEL::initClientListener);
        int index = 0;

        //S2C packets
        GILDED_GLORY_CHANNEL.registerS2CPacket(ItemUseSoundS2CPacket.class, index++);
        GILDED_GLORY_CHANNEL.registerS2CPacket(ChargingParticleS2CPacket.class, index++);
        GILDED_GLORY_CHANNEL.registerS2CPacket(IraedeusTargetS2CPacket.class, index++);

        //C2S packets
        GILDED_GLORY_CHANNEL.registerC2SPacket(UpdateTargetingC2SPacket.class, index++);
        GILDED_GLORY_CHANNEL.registerC2SPacket(UpdateReturningC2SPacket.class, index++);
    }
}
