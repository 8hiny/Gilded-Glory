package shiny.gildedglory.common.component.entity;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import shiny.gildedglory.common.item.FoolsArmorItem;
import shiny.gildedglory.common.registry.component.ModComponents;

public class FoolsStatueComponent implements TimedComponent, CommonTickingComponent, AutoSyncedComponent {

    private final LivingEntity provider;
    private int remainingTicks = 0;

    public FoolsStatueComponent(LivingEntity provider) {
        this.provider = provider;
    }

    @Override
    public void tick() {
        if (remainingTicks > 0) {
            remainingTicks--;

            if (!FoolsArmorItem.hasFullSet(provider)) this.disable();
        }
        else {
            this.disable();
        }

        ModComponents.FOOLS_STATUE.sync(provider);
    }

    @Override
    public void setDuration(int duration) {
        remainingTicks = duration;
    }

    @Override
    public int getDuration() {
        return remainingTicks;
    }

    @Override
    public void disable() {
        remainingTicks = 0;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        remainingTicks = tag.getInt("remainingTicks");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putInt("remainingTicks", remainingTicks);
    }
}
