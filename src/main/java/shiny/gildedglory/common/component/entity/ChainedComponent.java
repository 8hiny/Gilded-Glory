package shiny.gildedglory.common.component.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.CommonTickingComponent;
import shiny.gildedglory.common.registry.component.ModComponents;

import java.util.UUID;

public class ChainedComponent implements TimedComponent, CommonTickingComponent, AutoSyncedComponent {

    private final LivingEntity provider;
    private int remainingTicks = 0;
    private UUID counterpart;
    private boolean attacker = false;
    private int progress;
    private int cooldown;

    public ChainedComponent(LivingEntity provider) {
        this.provider = provider;
    }

    @Override
    public void tick() {
        if (this.cooldown > 0) this.cooldown--;
        else if (this.progress > 0) this.progress--;

        if (this.remainingTicks > 1) this.remainingTicks--;
    }

    @Override
    public void serverTick() {
        CommonTickingComponent.super.serverTick();

        LivingEntity entity = getCounterpartEntity();
        if (entity != null) {
            if (this.remainingTicks > 1 && (!entity.isAlive() || !this.provider.isAlive()) && entity instanceof LivingEntity) {
                ModComponents.CHAINED.get(entity).disable();
                this.disable();
            }
            else if (this.remainingTicks == 1) {
                ModComponents.CHAINED.get(entity).disable();
                this.disable();
            }
        }
        else if (this.counterpart != null) this.disable();
    }

    public UUID getCounterpart() {
        return this.counterpart;
    }

    public LivingEntity getCounterpartEntity() {
        ServerWorld world = (ServerWorld) this.provider.getWorld();

        if (this.counterpart != null && world.getEntity(this.counterpart) != null) {
            return (LivingEntity) world.getEntity(this.counterpart);
        }
        return null;
    }

    public void addProgress(LivingEntity entity, int amount) {
        this.progress = this.progress + amount;
        if (this.progress >= 100 && entity != null) this.chain(entity, this.progress * 4);
        else this.triggerCooldown();
    }

    public void chain(LivingEntity entity, int duration) {
        this.counterpart = entity.getUuid();
        this.setDuration(duration);
    }

    public void unChain() {
        LivingEntity entity = getCounterpartEntity();
        if (entity != null) {
            ModComponents.CHAINED.get(entity).disable();
        }
        this.disable();
    }

    public void triggerCooldown() {
        this.cooldown = 80;
    }

    @Override
    public void setDuration(int duration) {
        this.remainingTicks = duration;
        ModComponents.CHAINED.sync(provider);
    }

    @Override
    public int getDuration() {
        return this.remainingTicks;
    }

    public void setAttacker(boolean bl) {
        this.attacker = bl;
        ModComponents.CHAINED.sync(provider);
    }

    public boolean isAttacker() {
        return this.attacker;
    }

    @Override
    public void disable() {
        this.provider.getWorld().playSound(
                null,
                this.provider.getX(),
                this.provider.getY(),
                this.provider.getZ(),
                SoundEvents.ENTITY_ITEM_BREAK,
                this.provider.getSoundCategory(),
                1.0f,
                1.0f
        );

        this.counterpart = null;
        this.progress = 0;
        this.cooldown = 0;
        this.remainingTicks = 0;
        ModComponents.CHAINED.sync(provider);
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup wrapperLookup) {
        this.cooldown = tag.getInt("cooldown");
        this.progress = tag.getInt("progress");
        this.remainingTicks = tag.getInt("remainingTicks");
        this.attacker = tag.getBoolean("isAttacker");
        if (tag.contains("counterpart")) this.counterpart = tag.getUuid("counterpart");
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup wrapperLookup) {
        tag.putInt("cooldown", this.cooldown);
        tag.putInt("progress", this.progress);
        tag.putInt("remainingTicks", this.remainingTicks);
        tag.putBoolean("isAttacker", this.attacker);
        if (this.counterpart != null) tag.putUuid("counterpart", this.counterpart);
    }
}
