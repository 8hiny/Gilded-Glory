package shiny.gildedglory.common.entity;

import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import shiny.gildedglory.common.util.DynamicSoundSource;
import shiny.gildedglory.common.component.entity.ChainedComponent;
import shiny.gildedglory.common.item.CharmItem;
import shiny.gildedglory.common.registry.component.ModComponents;
import shiny.gildedglory.common.registry.damage_type.ModDamageTypes;
import shiny.gildedglory.common.registry.entity.ModEntities;
import team.lodestar.lodestone.systems.rendering.trail.TrailPoint;
import team.lodestar.lodestone.systems.rendering.trail.TrailPointBuilder;

import java.util.ArrayList;
import java.util.List;

public class SlashProjectileEntity extends PersistentProjectileEntity implements DynamicSoundSource {

    private static final TrackedData<Boolean> VERTICAL = DataTracker.registerData(SlashProjectileEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> CAN_CHAIN = DataTracker.registerData(SlashProjectileEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> VARIANT = DataTracker.registerData(SlashProjectileEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final EntityDimensions HORIZONTAL_DIMENSIONS = new EntityDimensions(2.4f, 0.5f, true);
    private static final EntityDimensions VERTICAL_DIMENSIONS = new EntityDimensions(2.4f, 1.0f, true);
    private final TrailPointBuilder builder = TrailPointBuilder.create(4);
    public final List<LivingEntity> hitEntities = new ArrayList<>();
    private float damage = 9.0f;
    private int life = 0;

    public SlashProjectileEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public SlashProjectileEntity(World world, Entity owner, double x, double y, double z, float damage, boolean vertical, boolean canChain) {
        super(ModEntities.SLASH_PROJECTILE, world);
        this.setPosition(x, y, z);
        this.setOwner(owner);
        this.damage = damage;

        this.dataTracker.set(VERTICAL, vertical);
        this.dataTracker.set(CAN_CHAIN, canChain);
        this.dataTracker.set(VARIANT, Math.random() <= 0.5 ? 1 : 0);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(VERTICAL, false);
        this.dataTracker.startTracking(CAN_CHAIN, false);
        this.dataTracker.startTracking(VARIANT, 0);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
        this.calculateDimensions();
    }

    @Override
    public void tick() {
        super.tick();

        builder.addTrailPoint(this.getPos());
        builder.tickTrailPoints();

        if (this.inGround || this.age > 20) {
            this.life++;
        }
        if (this.life >= 5) this.discard();

        LivingEntity attacker = (LivingEntity) this.getOwner();
        boolean bl = attacker instanceof PlayerEntity player && CharmItem.hasOwnedCharm(player);

        float damage = this.isVertical() ? this.damage : this.damage / 2;
        if (!this.getWorld().isClient()) {
            for (LivingEntity entity : this.getWorld().getEntitiesByClass(LivingEntity.class, getBoundingBox().expand(0.25), livingEntity -> (attacker != livingEntity))) {
                if (!this.hitEntities.contains(entity)) {

                    boolean bl1 = entity instanceof PlayerEntity player && CharmItem.hasOwnedCharm(player);
                    if (!bl || !bl1) {
                        DamageSource damageSource = new DamageSource(this.getWorld().getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(ModDamageTypes.SLASH), this, attacker == null ? this : attacker);

                        entity.damage(damageSource, damage);
                        if (attacker != null && this.canChain()) tryApplyChained(attacker, entity, damage);
                        this.hitEntities.add(entity);
                    }
                }
            }
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
    }

    protected void tryApplyChained(LivingEntity attacker, LivingEntity target, float value) {
        ChainedComponent attackerComponent = ModComponents.CHAINED.get(attacker);
        ChainedComponent targetComponent = ModComponents.CHAINED.get(target);

        if (attackerComponent.getDuration() == 0 && targetComponent.getDuration() == 0 && target.isAlive()) {
            targetComponent.addProgress(attacker, (int) value * 6);

            if (targetComponent.getDuration() > 0) {
                attackerComponent.chain(target, targetComponent.getDuration());
                attackerComponent.setAttacker(true);
                targetComponent.setAttacker(false);
            }
        }
    }

    public List<TrailPoint> getTrailPoints() {
        return this.builder.getTrailPoints();
    }

    public boolean isVertical() {
        return this.dataTracker.get(VERTICAL);
    }

    public boolean canChain() {
        return this.dataTracker.get(CAN_CHAIN);
    }

    public int getVariant() {
        return this.dataTracker.get(VARIANT);
    }

    @Override
    public Vec3d getPosition() {
        return this.getPos();
    }

    @Override
    public boolean canPlay() {
        return !this.isRemoved();
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("Vertical", this.dataTracker.get(VERTICAL));
        nbt.putBoolean("CanChain", this.dataTracker.get(CAN_CHAIN));
        nbt.putInt("Variant", this.dataTracker.get(VARIANT));
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);

        if (nbt.contains("Vertical")) {
            this.dataTracker.set(VERTICAL, nbt.getBoolean("Vertical"));
        }
        if (nbt.contains("CanChain")) {
            this.dataTracker.set(CAN_CHAIN, nbt.getBoolean("CanChain"));
        }
        this.dataTracker.set(VARIANT, nbt.getInt("Variant"));
    }

    @Override
    protected ItemStack asItemStack() {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean hasNoGravity() {
        return true;
    }

    @Override
    public boolean doesRenderOnFire() {
        return false;
    }

    @Override
    public float getBrightnessAtEyes() {
        return 1.0f;
    }

    @Override
    protected float getDragInWater() {
        return 0.99f;
    }

    @Override
    protected boolean tryPickup(PlayerEntity player) {
        return false;
    }

    @Override
    public boolean isSilent() {
        return true;
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return this.isVertical() ? VERTICAL_DIMENSIONS : HORIZONTAL_DIMENSIONS;
    }
}
