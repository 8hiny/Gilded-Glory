package shiny.gildedglory.common.entity;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.BlockPositionSource;
import net.minecraft.world.event.EntityPositionSource;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.PositionSourceType;
import shiny.gildedglory.GildedGlory;
import shiny.gildedglory.client.particle.effect.VectorParticleEffect;
import shiny.gildedglory.common.component.entity.IraedeusComponent;
import shiny.gildedglory.common.item.custom.ChargeableWeapon;
import shiny.gildedglory.common.registry.component.ModComponents;
import shiny.gildedglory.common.registry.damage_type.ModDamageTypes;
import shiny.gildedglory.common.registry.entity.ModEntities;
import shiny.gildedglory.common.registry.item.ModItems;
import shiny.gildedglory.common.registry.particle.ModParticles;
import shiny.gildedglory.common.registry.sound.ModSounds;
import shiny.gildedglory.common.util.DynamicSoundSource;
import shiny.gildedglory.common.util.GildedGloryUtil;
import team.lodestar.lodestone.systems.rendering.trail.TrailPoint;
import team.lodestar.lodestone.systems.rendering.trail.TrailPointBuilder;

import java.util.ArrayList;
import java.util.List;

public class IraedeusEntity extends ProjectileEntity implements FlyingItemEntity, DynamicSoundSource {

    private static final TrackedData<ItemStack> ITEM = DataTracker.registerData(IraedeusEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
    private static final TrackedData<Byte> IRAEDEUS_FLAGS = DataTracker.registerData(IraedeusEntity.class, TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Boolean> NO_CLIP = DataTracker.registerData(IraedeusEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> CHARGE = DataTracker.registerData(IraedeusEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<PositionSource> TARGET = DataTracker.registerData(IraedeusEntity.class, ExtraTrackedData.POSITION_SOURCE);
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
        this.dataTracker.startTracking(TARGET, new BlockPositionSource(new BlockPos(0, 0, 0)));
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

        if (this.activeTicks == 0) {
            GildedGloryUtil.startLoopingSound(this.getWorld(), this, GildedGlory.id("iraedeus_spin"));
        }

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

        boolean bl = false;
        LivingEntity owner = (LivingEntity) this.getOwner();
        for (LivingEntity entity : this.getWorld().getEntitiesByClass(LivingEntity.class, this.getBoundingBox(), this::canHit)) {
            if (!this.getWorld().isClient() && this.damageEntity(owner, entity)) bl = true;
        }
        if (bl) {
            ((ServerWorld) this.getWorld()).spawnParticles(ModParticles.IRAEDEUS_SLASH, this.getX(), this.getY(), this.getZ(), 0, 0, 0, 0, 0);
            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), ModSounds.IRAEDEUS_SLASH, SoundCategory.PLAYERS, 1.0f, 1.0f);
        }

        boolean bl1 = ((this.inGroundTime > 0 && !this.isTargeting()) || this.inGroundTime > 10);
        if (bl1 || owner == null || this.getPos().distanceTo(GildedGloryUtil.getThrowPos(owner, ModEntities.IRAEDEUS)) >= 48.0f) {
            if (!this.isReturning()) {
                float pitch = GildedGloryUtil.random(0.9f, 1.1f);
                this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), ModSounds.IRAEDEUS_HIT, SoundCategory.PLAYERS, 1.0f, pitch);

                this.spawnShockwaveParticle(this);
                this.setTargeting(false);
                this.hitEntities.clear();
            }
        }

        Pair<Boolean, Boolean> controlStatus = this.getControlStatus();
        if (this.age > 6) {
            if (controlStatus.getLeft() && !this.isParried() && owner != null) {
                Vec3d vec1 = GildedGloryUtil.getThrowPos(owner, ModEntities.IRAEDEUS).add(owner.getRotationVector().multiply(48.0));
                this.setTarget(this.handleTarget(owner, vec1));
                this.setTargeting(true);
                this.hitEntities.clear();
            }
            else if (controlStatus.getRight() && !this.isReturning() && !this.isParried()) {
                this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), ModSounds.IRAEDEUS_RETURN, SoundCategory.PLAYERS, 2.0f, 1.0f);
                this.setTargeting(false);
                this.hitEntities.clear();
            }
        }

        if (this.hasTarget()) {

            if (this.getTarget() == null) {
                GildedGlory.LOGGER.info("Target is null! Is client: " + this.getWorld().isClient());
            }

            if (this.targetTicks < 60 && this.getTarget() != null) {
                PositionSource source = this.getTarget();

                Vec3d target = owner.getPos();
                if (source.getPos(this.getWorld()).isPresent()) {
                    target = source.getPos(this.getWorld()).get();

                    if (!this.getWorld().isClient()) {
                        ((ServerWorld) this.getWorld()).spawnParticles(ParticleTypes.ENCHANTED_HIT, target.x, target.y, target.z, 1, 0.5, 1, 0.5, 0.02);
                    }
                }

                if (this.squaredDistanceTo(target) > 2.0f) {
                    this.moveToTarget(target, this.isParried());
                }
                else {
                    if (source.getType() == PositionSourceType.BLOCK) {
                        for (LivingEntity entity : this.getWorld().getEntitiesByClass(LivingEntity.class, this.getBoundingBox().expand(5.0f, 5.5f, 5.0f), this::canHit)) {
                            if (!this.getWorld().isClient() && this.damageEntity(owner, entity));
                        }

                        float pitch = GildedGloryUtil.random(0.9f, 1.1f);
                        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), ModSounds.IRAEDEUS_HIT, SoundCategory.PLAYERS, 1.0f, pitch);
                        this.spawnShockwaveParticle(this);
                    }
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
                    Vec3d vec3d = GildedGloryUtil.getThrowPos(owner, ModEntities.IRAEDEUS).subtract(this.getPos());
                    this.setPos(this.getX(), this.getY() + vec3d.y * 0.01, this.getZ());
                    this.setVelocity(this.getVelocity().multiply(0.8).add(vec3d.normalize().multiply(0.2 - this.getSpeed(false) * 0.01)));
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

    public boolean damageEntity(LivingEntity attacker, LivingEntity target) {
        DamageSource damageSource = new DamageSource(this.getWorld().getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(ModDamageTypes.IRAEDEUS), this, attacker == null ? this : attacker);
        float amount = (0.05f * this.getCharge() + 7.0f) + EnchantmentHelper.getAttackDamage(this.getStack(), target.getGroup());
        this.hitEntities.add(target);
        return target.damage(damageSource, amount);
    }

    public PositionSource handleTarget(Entity entity, Vec3d targetPos) {
        BlockHitResult blockHit = this.getWorld().raycast(new RaycastContext(entity.getEyePos(), targetPos, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this));
        LivingEntity target = GildedGloryUtil.raycastSingle(entity, this::canHit, entity.getRotationVector(), 0.6f, 48, true);

        if (target == null || !this.canHit(target)) {
            if (blockHit.getType() == HitResult.Type.MISS) {
                return new BlockPositionSource(blockHit.getBlockPos());
            }
            else {
                return new BlockPositionSource(blockHit.getBlockPos().add(blockHit.getSide().getVector()));
            }
        }
        else {
            return new EntityPositionSource(target, target.getHeight() / 2);
        }
    }

    public void moveToTarget(Vec3d targetPos, boolean invert) {
        Vec3d pos = this.getPos();

        double accuracy = (1 / (Math.sqrt(pos.squaredDistanceTo(targetPos)) * 0.5)) * this.getAccuracy(invert);
        Vec3d direction = targetPos.subtract(pos).multiply(accuracy);

        this.setVelocity(this.getVelocity().multiply(this.getAccuracy(true) - 0.3).add(direction).normalize().multiply(this.getSpeed(invert) * 0.85));
    }

    public void parry(Entity actor, Entity owner, float damage) {
        this.spawnShockwaveParticle(actor);
        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), ModSounds.IRAEDEUS_PARRY, SoundCategory.PLAYERS, 2.0f, 1.0f);

        this.setCharge(Math.max(0, this.getCharge() - (int) (damage * 2)));
        this.addVelocity(actor.getRotationVector().multiply(damage * 0.5));

        this.setTarget(new EntityPositionSource(owner, owner.getStandingEyeHeight()));
        this.setParried(true);
    }

    public Pair<Boolean, Boolean> getControlStatus() {
        boolean targeting = false;
        boolean returning = false;

        if (this.getOwner() instanceof PlayerEntity player) {
            IraedeusComponent component = ModComponents.IRAEDEUS.get(player);
                targeting = component.targeting;
                returning = component.returning;
                if (targeting) component.targetCooldown = 40;
        }
        return new Pair<>(targeting, returning);
    }

    public float getSpeed(boolean invert) {
        return invert ? -0.01f * this.getCharge() + 2 : 0.01f * this.getCharge() + 1.0f;
    }

    public float getAccuracy(boolean invert) {
        return invert ? -0.007f * this.getCharge() + 1.0f : 0.007f * this.getCharge() + 0.3f;
    }

    public void spawnShockwaveParticle(Entity from) {
        Vec3d rotation = from.getRotationVector();
        if (from == this) {
            rotation = this.getVelocity().normalize();
        }
        from.getWorld().addImportantParticle(new VectorParticleEffect(ModParticles.SHOCKWAVE, rotation.toVector3f(), 3.5f, 10),
                true, from.getX(), from.getEyeY(), from.getZ(), 0, 0, 0);
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

    public PositionSource getTarget() {
        return this.dataTracker.get(TARGET);
    }

    public void setTarget(PositionSource source) {
        this.dataTracker.set(TARGET, source);
    }

    public boolean insertStack(PlayerEntity player) {
        boolean bl;
        if (this.originalSlot != -1 && player.getInventory().getStack(this.originalSlot).isEmpty()) {
            bl = player.getInventory().insertStack(this.originalSlot, this.getStack());
        }
        else {
            bl = player.getInventory().insertStack(this.getStack());
        }

        if (bl) {
            ModComponents.IRAEDEUS.get(player).reset();
            player.getItemCooldownManager().set(ModItems.IRAEDEUS, (int) Math.max(20, this.activeTicks * 0.25f));
            if (this.getCharge() > 0) {
                GildedGloryUtil.sendSoundPackets(player.getWorld(), player, null, GildedGlory.id("iraedeus_hum"));
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

    @Override
    public boolean canPlay() {
        return !this.isRemoved();
    }
}
