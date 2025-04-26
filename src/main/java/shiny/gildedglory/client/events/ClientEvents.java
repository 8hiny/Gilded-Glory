package shiny.gildedglory.client.events;

import net.minecraft.client.MinecraftClient;
import shiny.gildedglory.common.component.entity.IraedeusComponent;
import shiny.gildedglory.common.util.DynamicSoundManager;

public class ClientEvents {

    public static void clientTick(MinecraftClient client) {
        DynamicSoundManager.getInstance().tick();
        IraedeusComponent.clientTick(client);
    }
}
