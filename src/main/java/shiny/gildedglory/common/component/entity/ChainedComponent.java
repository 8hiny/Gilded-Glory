package shiny.gildedglory.common.component.entity;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
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
        if (cooldown > 0) cooldown--;
        else if (progress > 0) progress--;

        if (remainingTicks > 1) remainingTicks--;
    }

    @Override
    public void serverTick() {
        CommonTickingComponent.super.serverTick();

        LivingEntity entity = getCounterpartEntity();
        if (entity != null) {
            if (remainingTicks > 1 && (!entity.isAlive() || !this.provider.isAlive()) && entity instanceof LivingEntity) {
                ModComponents.CHAINED.get(entity).disable();
                this.disable();
            }
            else if (remainingTicks == 1) {
                ModComponents.CHAINED.get(entity).disable();
                this.disable();
            }
        }
        else if (this.counterpart != null) this.disable();
    }

    public UUID getCounterpart() {
        return counterpart;
    }

    public LivingEntity getCounterpartEntity() {
        ServerWorld world = (ServerWorld) provider.getWorld();

        if (counterpart != null && world.getEntity(counterpart) != null) {
            return (LivingEntity) world.getEntity(counterpart);
        }
        return null;
    }

    public void addProgress(LivingEntity entity, int amount) {
        progress = progress + amount;
        if (progress >= 100 && entity != null) this.chain(entity, progress * 4);
        else this.triggerCooldown();
    }

    public void chain(LivingEntity entity, int duration) {
        counterpart = entity.getUuid();
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
        cooldown = 80;
    }

    @Override
    public void setDuration(int duration) {
        remainingTicks = duration;
        ModComponents.CHAINED.sync(provider);
    }

    @Override
    public int getDuration() {
        return remainingTicks;
    }

    public void setAttacker(boolean bl) {
        attacker = bl;
        ModComponents.CHAINED.sync(provider);
    }

    public boolean isAttacker() {
        return this.attacker;
    }

    @Override
    public void disable() {
        provider.getWorld().playSound(null, provider.getX(), provider.getY(), provider.getZ(), SoundEvents.ENTITY_ITEM_BREAK, provider.getSoundCategory(), 1.0f, 1.0f);

        counterpart = null;
        progress = 0;
        cooldown = 0;
        remainingTicks = 0;
        ModComponents.CHAINED.sync(provider);
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        cooldown = tag.getInt("cooldown");
        progress = tag.getInt("progress");
        remainingTicks = tag.getInt("remainingTicks");
        attacker = tag.getBoolean("isAttacker");
        if (tag.contains("counterpart")) counterpart = tag.getUuid("counterpart");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putInt("cooldown", cooldown);
        tag.putInt("progress", progress);
        tag.putInt("remainingTicks", remainingTicks);
        tag.putBoolean("isAttacker", attacker);
        if (counterpart != null) tag.putUuid("counterpart", counterpart);
    }
}
