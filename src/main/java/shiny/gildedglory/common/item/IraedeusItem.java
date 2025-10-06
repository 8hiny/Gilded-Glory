package shiny.gildedglory.common.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import shiny.gildedglory.client.pose.ArmPose;
import shiny.gildedglory.common.component.entity.IraedeusComponent;
import shiny.gildedglory.common.item.custom.ChargeableWeapon;
import shiny.gildedglory.common.item.custom.CustomAttackWeapon;
import shiny.gildedglory.common.registry.item.ModItems;
import shiny.gildedglory.common.registry.particle.ModParticles;
import shiny.gildedglory.common.registry.sound.ModSounds;

public class IraedeusItem extends SheathableSwordItem implements ChargeableWeapon, CustomAttackWeapon {

    //TODO

    public IraedeusItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }

    //For usageTick():
    //Once sheathed and i == 15, set this.sheathing to false (the player is still using the item, which means they
    //want to charge it
    //Once sheathed and i >= 40 (fully charged), left clicking starts the wide slash, letting go starts the area slash

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);

        if (entity instanceof LivingEntity) {
            long lastAttackTime = IraedeusComponent.get((LivingEntity) entity).getLastAttackTime();
            if (entity.age - lastAttackTime >= 200 && world.getTime() % 5 == 0) {
                ChargeableWeapon.tickCharge(stack);
            }
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
