package shiny.gildedglory.common.registry.entity;

import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.world.event.PositionSource;

public class ModTrackedDataHandlers {

    public static final TrackedDataHandler<PositionSource> POSITION_SOURCE = (TrackedDataHandler.ImmutableHandler<PositionSource>) () -> PositionSource.PACKET_CODEC;

    static {
        TrackedDataHandlerRegistry.register(POSITION_SOURCE);
    }

    private ModTrackedDataHandlers() {
    }
}
