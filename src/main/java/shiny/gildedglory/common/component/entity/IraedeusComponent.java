package shiny.gildedglory.common.component.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.CommonTickingComponent;
import shiny.gildedglory.GildedGlory;
import shiny.gildedglory.common.item.IraedeusItem;
import shiny.gildedglory.common.registry.component.ModComponents;

public class IraedeusComponent implements AutoSyncedComponent, CommonTickingComponent {

    private final LivingEntity provider;
    private IraedeusItem.AttackType lastAttack;
    private long lastAttackTime;
    private boolean usedWhenSheathed;
    private int dashTicks;

    public IraedeusComponent(LivingEntity provider) {
        this.provider = provider;
    }

    public static IraedeusComponent get(LivingEntity provider) {
        return ModComponents.IRAEDEUS.get(provider);
    }

    public void sync() {
        ModComponents.IRAEDEUS.sync(this.provider);
    }

    @Override
    public void tick() {
        if (this.dashTicks > -1 && this.dashTicks < 15) {
            this.dashTicks++;
        }
        else if (this.dashTicks == 15) {
            this.dashTicks = -1;
        }

        if (this.usedWhenSheathed && !this.provider.isUsingItem()) {
            this.usedWhenSheathed = false;
        }
        this.sync();
    }

    @Override
    public void readFromNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        this.lastAttackTime = nbtCompound.getLong("LastAttackTime");
        this.usedWhenSheathed = nbtCompound.getBoolean("UsedWhenSheathed");
        this.dashTicks = nbtCompound.getInt("DashTicks");
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        nbtCompound.putLong("LastAttackTime", this.lastAttackTime);
        nbtCompound.putBoolean("UsedWhenSheathed", this.usedWhenSheathed);
        nbtCompound.putInt("DashTicks", this.dashTicks);
    }

    public void updateLastAttack(IraedeusItem.AttackType lastAttack) {
        this.lastAttack = lastAttack;
        this.lastAttackTime = this.provider.age;
        this.sync();
    }

    public IraedeusItem.AttackType getLastAttack() {
        return this.lastAttack;
    }

    public long getLastAttackTime() {
        return this.lastAttackTime;
    }

    public boolean usedWhenSheathed() {
        return this.usedWhenSheathed;
    }

    public void setUsedWhenSheathed() {
        this.usedWhenSheathed = true;
        this.sync();
    }

    public void updateLastDash() {
        this.dashTicks = 0;
        this.sync();
    }

    public int getDashTicks() {
        return this.dashTicks;
    }
}
