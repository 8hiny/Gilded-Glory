package shiny.gildedglory.common.component.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import shiny.gildedglory.common.item.IraedeusItem;
import shiny.gildedglory.common.registry.component.ModComponents;

public class IraedeusComponent implements AutoSyncedComponent {

    private final LivingEntity provider;
    private IraedeusItem.AttackType lastAttack;
    private long lastAttackTime;

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
    public void readFromNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        this.lastAttackTime = nbtCompound.getLong("LastAttackTime");
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        nbtCompound.putLong("LastAttackTime", this.lastAttackTime);
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
}
