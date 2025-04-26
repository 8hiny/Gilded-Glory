package shiny.gildedglory.common.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import shiny.gildedglory.GildedGlory;
import shiny.gildedglory.common.component.entity.IraedeusComponent;
import shiny.gildedglory.common.item.ChargeableWeapon;
import shiny.gildedglory.common.registry.component.ModComponents;
import shiny.gildedglory.common.registry.damage_type.ModDamageTypes;
import shiny.gildedglory.common.registry.entity.ModEntities;
import shiny.gildedglory.common.registry.item.ModItems;
import shiny.gildedglory.common.util.DynamicSoundSource;
import shiny.gildedglory.common.util.GildedGloryUtil;
import team.lodestar.lodestone.systems.rendering.trail.TrailPoint;
import team.lodestar.lodestone.systems.rendering.trail.TrailPointBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//The entity for a summoned/thrown iraedeus
//To-Do:
//Summoned state for when the weapon is summoned and idle (not being controlled)
//AoE damage functionality when hit block while targeting
//Particles for: throwing, returning, targeting, parrying, block hit while targeting
//Sounds for: throwing, returning, targeting, parrying, block hit while targeting, entity hit

//Make the slot of the owner component reset when the entity dies or is removed some other way
//Make the parry send the weapon towards its owner

public class IraedeusEntity extends ProjectileEntity implements FlyingItemEntity, DynamicSoundSource {

    private static final TrackedData<ItemStack> ITEM = DataTracker.registerData(IraedeusEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
    private static final TrackedData<Byte> IRAEDEUS_FLAGS = DataTracker.registerData(IraedeusEntity.class, TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Boolean> NO_CLIP = DataTracker.registerData(IraedeusEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> CHARGE = DataTracker.registerData(IraedeusEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<HomingTarget> TARGET = DataTracker.registerData(IraedeusEntity.class, ExtraTrackedData.TARGET);
    private static final int RETURNING_FLAG = 1;
    private static final int TARGETING_FLAG = 2;
    private static final int PARRIED_FLAG = 4;
    private final TrailPointBuilder builder = TrailPointBuilder.create(16);
    private final int originalSlot;
    private Vec3d initialVelocity = Vec3d.ZERO;
    private int targetTicks;
    private int activeTicks;
    protected int inGroundTime;
    private final List<Entity> hitEntities = new ArrayList<>();
    private HomingTarget target;

    public IraedeusEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
        this.originalSlot = -1;
    }

    public IraedeusEntity(World world, Entity owner, int slot, double x, double y, double z) {
        super(ModEntities.IRAEDEUS, world);
        this.originalSlot = slot;

        this.setPos(x, y, z);
        this.setOwner(owner);
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(ITEM, ItemStack.EMPTY);
        this.dataTracker.startTracking(IRAEDEUS_FLAGS, (byte) 0);
        this.dataTracker.startTracking(NO_CLIP, false);
        this.dataTracker.startTracking(CHARGE, 0);
        this.dataTracker.startTracking(TARGET, new HomingTarget(Vec3d.ZERO));
    }

    @Override
    public boolean shouldRender(double distance) {
        double d = this.getBoundingBox().getAverageSideLength() * 10.0;
        if (Double.isNaN(d)) d = 1.0;

        d *= 64.0 * getRenderDistanceMultiplier();
        return distance < d * d;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.checkGroundCollision()) this.inGroundTime++;
        else this.inGroundTime = 0;

        Vec3d vec = this.getVelocity();
        if (this.prevPitch == 0.0f && this.prevYaw == 0.0f) {
            double d = vec.horizontalLength();
            this.setYaw((float) (MathHelper.atan2(vec.x, vec.z) * 180.0f / (float) Math.PI));
            this.setPitch((float) (MathHelper.atan2(vec.y, d) * 180.0f / (float) Math.PI));
            this.prevYaw = this.getYaw();
            this.prevPitch = this.getPitch();
        }

        LivingEntity owner = (LivingEntity) this.getOwner();
        for (LivingEntity entity : this.getWorld().getEntitiesByClass(LivingEntity.class, getBoundingBox(), this::canHit)) {
            if (!this.getWorld().isClient()) this.damage(owner, entity);
        }

        boolean bl = ((this.targetTicks == 0 && this.inGroundTime > 0) || (this.targetTicks > 0 && this.inGroundTime >= 10));
        if (bl || owner == null || this.getPos().distanceTo(GildedGloryUtil.getThrowPos(owner, this)) >= 48.0f) {
            this.setTargeting(false);
        }

        Pair<Boolean, Boolean> controlStatus = this.getControlStatus();
        if (this.age > 6) {
            if (controlStatus.getLeft() && !this.isParried() && owner != null) {
                Vec3d vec1 = GildedGloryUtil.getThrowPos(owner, this).add(owner.getRotationVector().multiply(48.0));
                this.setTarget(this.handleTarget(owner, vec1, vec1.add(owner.getRotationVector().multiply(8.0))));
                this.setTargeting(true);
                this.targetTicks = 0;

                vec1 = this.getTarget().pos();
                this.getWorld().addImportantParticle(ParticleTypes.FLASH, vec1.x, vec1.y, vec1.z, 0, 0, 0);
            }
            else if (controlStatus.getRight() && !this.isReturning() && !this.isParried()) {
                this.setTargeting(false);
            }
        }

        if (this.hasTarget()) {
            if (this.targetTicks < 60) {
                HomingTarget target = this.getTarget();

                Vec3d vec1 = target.pos();
                this.getWorld().addImportantParticle(ParticleTypes.FLASH, vec1.x, vec1.y, vec1.z, 0, 0, 0);

                if (this.squaredDistanceTo(target.pos()) > 2.0f) {
                    this.moveToTarget(target, this.isParried());
                }
                else {
                    this.setTargeting(false);
                    this.setParried(false);
                }
                this.targetTicks++;
            }
            else {
                this.setTargeting(false);
                this.setParried(false);
            }
        }
        else if (this.isReturning()) {
            if (owner != null) {
                if (owner.isAlive()) {
                    Vec3d vec3d = GildedGloryUtil.getThrowPos(owner, this).subtract(this.getPos());
                    this.setPos(this.getX(), this.getY() + vec3d.y * 0.01, this.getZ());
                    this.setVelocity(this.getVelocity().multiply(0.95).add(vec3d.normalize().multiply(0.2 - this.getSpeed(false) * 0.015)));
                }
                else if (owner instanceof PlayerEntity player) {
                    this.insertStack(player);
                    this.discard();
                }
                else {
                    this.dropStack(this.getStack());
                    this.discard();
                }
            }
            else if (this.age - this.activeTicks > 40) {
                this.dropStack(this.getStack());
                this.discard();
            }
        }
        else {
            if (this.initialVelocity == Vec3d.ZERO) this.initialVelocity = this.getVelocity();
            this.setVelocity(this.initialVelocity.multiply(this.getSpeed(false)));

            if (this.getCharge() > 0) this.setCharge(this.getCharge() - 1);
        }

        if (this.isReturning()){
            vec = this.getVelocity();
        }
        else {
            vec = Entity.adjustMovementForCollisions(this, this.getVelocity(), this.getBoundingBox(), this.getWorld(), List.of());
        }

        double d = this.getX() + vec.x;
        double e = this.getY() + vec.y;
        double f = this.getZ() + vec.z;
        double g = vec.horizontalLength();

        this.setPitch((float) (MathHelper.atan2(vec.y, g) * 180.0f / (float) Math.PI));
        this.setYaw((float) (MathHelper.atan2(vec.x, vec.z) * 180.0f / (float) Math.PI));

        if (this.isTouchingWater()) {
            for (int i = 0; i < 4; i++) {
                this.getWorld().addParticle(ParticleTypes.BUBBLE, vec.x - d * 0.25, vec.y - e * 0.25, vec.z - f * 0.25, d, e, f);
            }
        }

        this.setPosition(d, e, f);
        this.checkBlockCollision();

        builder.addTrailPoint(this.getPos());
        builder.tickTrailPoints();

        if (!this.isReturning()) this.activeTicks++;
    }

    public boolean checkGroundCollision() {
        Vec3d vec = this.getPos();
        Vec3d vec1 = vec.add(this.getVelocity());
        BlockHitResult hit = this.getWorld().raycast(new RaycastContext(vec, vec1, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this));
        return hit.getType() == HitResult.Type.BLOCK && !this.getWorld().getBlockState(hit.getBlockPos()).getCollisionShape(this.getWorld(), hit.getBlockPos()).isEmpty();
    }

    public void damage(LivingEntity attacker, LivingEntity target) {
        DamageSource damageSource = new DamageSource(this.getWorld().getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(ModDamageTypes.IRAEDEUS), this, attacker == null ? this : attacker);
        target.damage(damageSource, 0.07f * this.getCharge() + 4.0f);
        this.hitEntities.add(target);
    }

    public HomingTarget handleTarget(Entity entity, Vec3d targetPos, Vec3d fallback) {
        Vec3d direction = targetPos.subtract(entity.getEyePos());
        Box box = entity.getBoundingBox().stretch(direction).expand(1.0, 1.0, 1.0);

        EntityHitResult entityHit = ProjectileUtil.raycast(entity, entity.getEyePos(), targetPos, box, entity1 -> entity1 != this && entity1 != entity, direction.lengthSquared());
        BlockHitResult blockHit = this.getWorld().raycast(new RaycastContext(entity.getEyePos(), targetPos, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this));

        if (entityHit == null) {
            if (blockHit.getType() == HitResult.Type.MISS) {
                return new HomingTarget(fallback);
            }
            else {
                return new HomingTarget(
                        blockHit.getBlockPos().toCenterPos().add(Vec3d.of(blockHit.getSide().getVector()))
                );
            }
        }
        else {
            return new HomingTarget(entityHit.getEntity());
        }
    }

    public void moveToTarget(HomingTarget target, boolean invert) {
        Vec3d pos = this.getPos();
        Vec3d targetPos = target.pos();

        double accuracy = (1 / (Math.sqrt(pos.squaredDistanceTo(targetPos)) * 0.5)) * this.getAccuracy(invert);
        Vec3d direction = target.pos().subtract(pos).multiply(accuracy);

        this.setVelocity(this.getVelocity().add(direction).normalize().multiply(this.getSpeed(invert)));
    }

    public void parry(Entity actor, Entity owner, float damage) {
        actor.getWorld().addImportantParticle(ParticleTypes.FLASH, actor.getX(), actor.getEyeY(), actor.getZ(), 0, 0, 0);

        this.addVelocity(actor.getRotationVector().multiply(damage * 0.5));
        this.setTarget(new HomingTarget(GildedGloryUtil.getThrowPos(owner, this)));
        this.setParried(true);
    }

    public Pair<Boolean, Boolean> getControlStatus() {
        boolean targeting = false;
        boolean returning = false;

        if (this.getOwner() instanceof PlayerEntity player) {
            IraedeusComponent component = ModComponents.IRAEDEUS.get(player);
            if (player.getInventory().selectedSlot == component.slot) {
                targeting = component.targeting;
                returning = component.returning;
            }
        }
        return new Pair<>(targeting, returning);
    }

    public float getSpeed(boolean invert) {
        return invert ? -0.0125f * this.getCharge() + 2 : 0.0125f * this.getCharge() + 0.75f;
    }

    public float getAccuracy(boolean invert) {
        return invert ? -0.007f * this.getCharge() + 0.85f : 0.007f * this.getCharge() + 0.15f;
    }

    @Override
    protected boolean canHit(Entity entity) {
        return super.canHit(entity) && entity != this.getOwner() && (this.hitEntities.isEmpty() || !this.hitEntities.contains(entity));
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        if (!this.getWorld().isClient() && this.isNoClip() && (this.isOwner(player) || this.getOwner() == null)) {
            if (this.insertStack(player)) {
                player.sendPickup(this, 1);
                this.discard();
            }
        }
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (!isInvulnerableTo(source)) {
            Entity attacker = source.getAttacker();
            if (attacker != null && !this.isOwner(attacker)) {
                this.scheduleVelocityUpdate();
                this.parry(attacker, this.getOwner(), amount);
                return true;
            }
        }
        return false;
    }

    public List<TrailPoint> getTrailPoints() {
        return this.builder.getTrailPoints();
    }

    public ItemStack getItem() {
        return this.dataTracker.get(ITEM);
    }

    public void setItem(ItemStack stack) {
        this.dataTracker.set(ITEM, stack.copyWithCount(1));
        this.setCharge(ChargeableWeapon.getCharge(stack));
    }

    public boolean isReturning() {
        byte b = this.dataTracker.get(IRAEDEUS_FLAGS);
        return (b & RETURNING_FLAG) != 0;
    }

    public boolean isTargeting() {
        byte b = this.dataTracker.get(IRAEDEUS_FLAGS);
        return (b & TARGETING_FLAG) != 0;
    }

    public boolean isParried() {
        byte b = this.dataTracker.get(IRAEDEUS_FLAGS);
        return (b & PARRIED_FLAG) != 0;
    }

    public boolean hasTarget() {
        return this.isTargeting() || this.isParried();
    }

    public void setReturning(boolean returning) {
        this.setIraedeusFlag(RETURNING_FLAG, returning);
        this.setNoClip(returning);

        if (returning) this.hitEntities.clear();
    }

    public void setTargeting(boolean targeting) {
        this.setIraedeusFlag(TARGETING_FLAG, targeting);
        this.setReturning(!targeting);
        if (targeting) this.targetTicks = 0;
    }

    public void setParried(boolean parried) {
        this.setIraedeusFlag(PARRIED_FLAG, parried);
    }

    private void setIraedeusFlag(int index, boolean flag) {
        byte b = this.dataTracker.get(IRAEDEUS_FLAGS);
        if (flag) {
            this.dataTracker.set(IRAEDEUS_FLAGS, (byte) (b | index));
        } else {
            this.dataTracker.set(IRAEDEUS_FLAGS, (byte) (b & ~index));
        }
    }

    public boolean isNoClip() {
        return !this.getWorld().isClient ? this.noClip : this.dataTracker.get(NO_CLIP);
    }

    public void setNoClip(boolean noClip) {
        this.noClip = noClip;
        this.dataTracker.set(NO_CLIP, noClip);
    }

    public int getCharge() {
        return this.dataTracker.get(CHARGE);
    }

    public void setCharge(int charge) {
        this.dataTracker.set(CHARGE, charge);
    }

    public HomingTarget getTarget() {
        return this.dataTracker.get(TARGET);
    }

    public void setTarget(HomingTarget target) {
        this.dataTracker.set(TARGET, target);
    }

    public boolean insertStack(PlayerEntity player) {
        boolean bl;
        if (player.getInventory().getStack(this.originalSlot).isEmpty()) {
            bl = player.getInventory().insertStack(this.originalSlot, this.getStack());
        }
        else {
            bl = player.getInventory().insertStack(this.getStack());
        }

        if (bl) {
            ModComponents.IRAEDEUS.get(player).reset();
            player.getItemCooldownManager().set(ModItems.IRAEDEUS, (int) Math.max(20, this.activeTicks * 0.25f));
            if (this.getCharge() > 0) {
                GildedGloryUtil.startLoopingSound(player.getWorld(), player, GildedGlory.id("iraedeus_hum"));
            }
        }
        return bl;
    }

    @Override
    public ItemStack getStack() {
        ItemStack stack = this.getItem();
        ChargeableWeapon.setCharge(stack, this.getCharge());
        return stack.isEmpty() ? new ItemStack(ModItems.IRAEDEUS) : stack;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        ItemStack itemStack = this.getItem();
        if (!itemStack.isEmpty()) {
            nbt.put("Item", itemStack.writeNbt(new NbtCompound()));
        }
        nbt.putByte("Status", this.dataTracker.get(IRAEDEUS_FLAGS));
        nbt.putInt("Charge", this.getCharge());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        ItemStack itemStack = ItemStack.fromNbt(nbt.getCompound("Item"));
        this.setItem(itemStack);
        this.dataTracker.set(IRAEDEUS_FLAGS, nbt.getByte("Status"));
        this.setCharge(nbt.getInt("Charge"));
    }

    @Override
    protected Entity.MoveEffect getMoveEffect() {
        return Entity.MoveEffect.NONE;
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
    public boolean isSilent() {
        return true;
    }

    @Override
    public boolean canHit() {
        return true;
    }

    @Override
    public Vec3d getPosition() {
        return this.getPos();
    }

    //This should be used to play a resonating and spinning sound when the entity swooshes by the listener
    @Override
    public boolean canPlay() {
        return !this.isRemoved();
    }
}
