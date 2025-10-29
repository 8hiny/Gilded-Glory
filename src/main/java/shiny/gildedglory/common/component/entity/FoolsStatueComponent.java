package shiny.gildedglory.common.component.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.CommonTickingComponent;
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
        if (this.remainingTicks > 0) {
            this.remainingTicks--;

            if (!FoolsArmorItem.hasFullSet(this.provider)) this.disable();
        }
        else {
            this.disable();
        }

        ModComponents.FOOLS_STATUE.sync(this.provider);
    }

    @Override
    public void setDuration(int duration) {
        this.remainingTicks = duration;
    }

    @Override
    public int getDuration() {
        return this.remainingTicks;
    }

    @Override
    public void disable() {
        this.remainingTicks = 0;
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup wrapperLookup) {
        this.remainingTicks = tag.getInt("remainingTicks");
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup wrapperLookup) {
        tag.putInt("remainingTicks", this.remainingTicks);
    }
}
