package shiny.gildedglory.common.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import shiny.gildedglory.GildedGlory;
import shiny.gildedglory.client.particle.effect.VectorParticleEffect;
import shiny.gildedglory.common.component.entity.IraedeusComponent;
import shiny.gildedglory.common.item.custom.ChargeableWeapon;
import shiny.gildedglory.common.item.custom.CustomAttackWeapon;
import shiny.gildedglory.common.item.custom.SheathableWeapon;
import shiny.gildedglory.common.registry.damage_type.ModDamageTypes;
import shiny.gildedglory.common.registry.item.ModItems;
import shiny.gildedglory.common.registry.particle.ModParticles;
import shiny.gildedglory.common.registry.sound.ModSounds;
import shiny.gildedglory.common.util.GildedGloryUtil;

public class IraedeusItem extends SheathableSwordItem implements ChargeableWeapon, CustomAttackWeapon {

    //TODO This is not synced to other clients - need to figure out another way of storing this boolean
    private boolean playedSound;

    public IraedeusItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (user.getOffHandStack() != stack && this.isSheathed(user, stack)) {
            IraedeusComponent.get(user).setUsedWhenSheathed();
        }
        return super.use(world, user, hand);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        super.usageTick(world, user, stack, remainingUseTicks);
        int i = this.getMaxUseTime(stack, user) - remainingUseTicks;
        if (!this.playedSound && this.isSheathed(user, stack) && (i == 5 || i == 15)) {
            GildedGloryUtil.playLoopingSound(world, user, GildedGlory.id("iraedeus_charging"));
            this.playedSound = true;
        }
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        int i = this.getMaxUseTime(stack, user) - remainingUseTicks;

        if (this.isSheathed(user, stack) && (!this.currentlySheathing(user, stack) || IraedeusComponent.get(user).usedWhenSheathed())) {
            if (i >= 40) {
                //Left and right click actions here
            }
            else if (i < 4) {
                if (!user.isSneaking()) {
                    user.fallDistance = 0;
                    IraedeusComponent.get(user).updateLastDash();

                    if (world.isClient()) {
                        user.setVelocity(user.getRotationVector().multiply(1.5));
                    }
                    if (user instanceof PlayerEntity player) {
                        player.getItemCooldownManager().set(this, 100);
                    }
                }
                else {
                    //Maybe add an unsheathing animation here
                    user.swingHand(Hand.MAIN_HAND, user instanceof ServerPlayerEntity);
                    SheathableWeapon.setSheathed(stack, false);
                }
            }
        }
        super.onStoppedUsing(stack, world, user, remainingUseTicks);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);

        if (entity instanceof LivingEntity livingEntity) {
            IraedeusComponent component = IraedeusComponent.get(livingEntity);

            long lastAttackTime = component.getLastAttackTime();
            if (entity.age - lastAttackTime >= 200 && world.getTime() % 5 == 0) {
                ChargeableWeapon.tickCharge(stack);
            }

            if (this.isSheathed(livingEntity, stack) && !this.currentlySheathing(livingEntity, stack)) {
                if (component.getDashTicks() == 15 || (component.getDashTicks() >= 5 && entity.isOnGround())) {
                    Vec3d velocity = livingEntity.getVelocity();
                    if (world instanceof ServerWorld serverWorld) {
                        int i = 0;
                        Box range = livingEntity.getBoundingBox().stretch(velocity).expand(3).offset(velocity.add(livingEntity.getRotationVector().multiply(2.5)));
                        for (LivingEntity target : world.getEntitiesByClass(LivingEntity.class, range,
                                livingEntity1 -> livingEntity1.canHit() && livingEntity.canSee(livingEntity1) && livingEntity1 != livingEntity
                        )) {
                            i++;
                            DamageSource source = new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(ModDamageTypes.IRAEDEUS_DASH_SLASH));
                            target.damage(source, 12);
                        }

                        if (i > 0) {
                            Vec3d pos = range.getCenter();
                            Vec3d rotation = livingEntity.getRotationVector();

                            //TODO Add dash and dash slash sounds
                            serverWorld.spawnParticles(
                                    new VectorParticleEffect(ModParticles.LARGE_IRAEDEUS_SLASH, rotation.toVector3f(), 4.5f, 5),
                                    pos.x, pos.y, pos.z,
                                    2,
                                    0, 0, 0,
                                    0
                            );
                            component.updateLastAttack(AttackType.DASH);
                            ChargeableWeapon.addCharge(stack, AttackType.DASH.getValue() * Math.max(2, i / 2));
                        }
                        livingEntity.swingHand(Hand.MAIN_HAND, livingEntity instanceof ServerPlayerEntity);
                    }
                    SheathableWeapon.setSheathed(stack, false);
                }
            }
        }

        if (this.playedSound && !entity.isUsing(this)) {
            this.playedSound = false;
        }
    }

    @Override
    public AttackContext onAttack(ItemStack stack, LivingEntity attacker, Entity target, DamageSource source, float amount, boolean critical, boolean sweeping) {
        if (!attacker.getWorld().isClient()) {
            IraedeusComponent component = IraedeusComponent.get(attacker);
            AttackType lastAttack = component.getLastAttack();
            AttackType current = AttackType.NONE;

            if (critical) {
                current = AttackType.CRITICAL;
            }
            else if (sweeping) {
                current = AttackType.SWEEPING;
            }
            else if (attacker.isSprinting()) {
                current = AttackType.KNOCKBACK;
            }

            if (lastAttack != current) {
                ChargeableWeapon.addCharge(stack, current.getValue() * 2);
            }
            component.updateLastAttack(current);
        }
        return new AttackContext(stack, attacker, target, source, amount, true);
    }

    @Override
    public int getMaxCharge() {
        return 150;
    }

    @Override
    public boolean offHandUsable() {
        return false;
    }

    @Override
    public boolean chargeWhileUsing() {
        return false;
    }

    @Override
    public Item getSheath(ItemStack stack) {
        return ModItems.IRAEDEUS_SHEATH;
    }

    @Override
    public boolean allowComponentsUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        return !canLoseCharge(newStack);
    }

    @Override
    public Text getName(ItemStack stack) {
        return Text.translatable(this.getTranslationKey(stack)).setStyle(Style.EMPTY.withColor(0x4A626A));
    }

    @Override
    public SoundEvent getCritAttackSound(ItemStack stack) {
        float f = ChargeableWeapon.getChargePercentage(stack);

        if (f <= 0.5f) return ModSounds.IRAEDEUS_CRIT_LOW;
        else if (f <= 0.75f) return ModSounds.IRAEDEUS_CRIT_MEDIUM;
        else return ModSounds.IRAEDEUS_CRIT_HIGH;
    }

    @Override
    public SoundEvent getSweepAttackSound(ItemStack stack) {
        float f = ChargeableWeapon.getChargePercentage(stack);

        if (f <= 0.5f) return ModSounds.IRAEDEUS_SWEEP_LOW;
        else if (f <= 0.75f) return ModSounds.IRAEDEUS_SWEEP_MEDIUM;
        else return ModSounds.IRAEDEUS_SWEEP_HIGH;
    }

    @Override
    public SoundEvent getKnockbackAttackSound(ItemStack stack) {
        float f = ChargeableWeapon.getChargePercentage(stack);

        if (f <= 0.5f) return ModSounds.IRAEDEUS_KNOCKBACK_LOW;
        else if (f <= 0.75f) return ModSounds.IRAEDEUS_KNOCKBACK_MEDIUM;
        else return ModSounds.IRAEDEUS_KNOCKBACK_HIGH;
    }

    @Override
    public boolean hideOffHandItem(LivingEntity holder, ItemStack stack) {
        return this.isSheathed(holder, stack);
    }

    @Override
    public SimpleParticleType getSweepAttackParticle(ItemStack stack) {
        return ModParticles.IRAEDEUS_SLASH;
    }

    @Override
    public SimpleParticleType getCritAttackParticle(ItemStack stack) {
        return ModParticles.IRAEDEUS_VERTICAL_SLASH;
    }

    public enum AttackType {
        NONE(0),
        SWEEPING(1),
        KNOCKBACK(1),
        CRITICAL(2),
        DAGGER(1),
        DASH(3),
        AREA(3),
        WIDE(3),
        BEAM(4);

        private final int value;

        private AttackType(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }
}
