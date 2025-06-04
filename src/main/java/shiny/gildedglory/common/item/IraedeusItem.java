package shiny.gildedglory.common.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import shiny.gildedglory.GildedGlory;
import shiny.gildedglory.common.component.entity.IraedeusComponent;
import shiny.gildedglory.common.entity.IraedeusEntity;
import shiny.gildedglory.common.item.custom.ChargeableWeapon;
import shiny.gildedglory.common.item.custom.CustomAttackWeapon;
import shiny.gildedglory.common.item.custom.CustomEffectsWeapon;
import shiny.gildedglory.common.registry.component.ModComponents;
import shiny.gildedglory.common.registry.entity.ModEntities;
import shiny.gildedglory.common.registry.particle.ModParticles;
import shiny.gildedglory.common.registry.sound.ModSounds;
import shiny.gildedglory.common.util.GildedGloryUtil;

import java.util.List;

public class IraedeusItem extends SwordItem implements CustomAttackWeapon, CustomEffectsWeapon, ChargeableWeapon {

    //Weapon color: #4a626a
    //Saturated weapon color: #19596e

    public IraedeusItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (user.getOffHandStack() == stack) {
            return TypedActionResult.fail(stack);
        }
        else {
            this.spawnIraedeus(world, user, stack);
            stack.decrement(1);

            user.setCurrentHand(hand);
            return TypedActionResult.success(stack);
        }
    }

    public void spawnIraedeus(World world, PlayerEntity user, ItemStack stack) {
        if (!world.isClient()) {
            int slot = -1;
            for (int i = 0; i < user.getInventory().size(); i++) {
                if (user.getInventory().getStack(i) == stack) {
                    slot = i;
                    break;
                }
            }
            IraedeusComponent component = ModComponents.IRAEDEUS.get(user);
            component.setSlot(slot);

            //Commented out cause unsure about summoning feature
            //if (!user.isSneaking()) {
            Vec3d pos = GildedGloryUtil.getThrowPos(user, ModEntities.IRAEDEUS);
            IraedeusEntity iraedeus = new IraedeusEntity(world, user, slot, pos.x, pos.y, pos.z);
            iraedeus.setVelocity(user, user.getPitch(), user.getYaw(), user.getRoll(), 1.0f, 0.0f);
            iraedeus.setItem(stack);
            world.spawnEntity(iraedeus);
            component.setEntity(iraedeus.getUuid());

            float pitch = GildedGloryUtil.random(0.95f, 1.05f);
            world.playSound(null, user.getX(), user.getY(), user.getZ(), ModSounds.IRAEDEUS_THROW, SoundCategory.PLAYERS, 1.0f, pitch);
            //}
            //else {
                //component.setSummoned(true);
                //component.setStack(stack);
            //}
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (getCooldown(stack) > 0) setCooldown(stack, getCooldown(stack) - 1);
        else ChargeableWeapon.tickCharge(stack);

        if (entity instanceof LivingEntity livingEntity && livingEntity.getMainHandStack() == stack && ChargeableWeapon.getCharge(stack) > 0 && world.getTime() % 4 == 0) {
            this.addChargeParticles(world, entity, ChargeableWeapon.getChargePercentage(stack));
        }
    }

    public void addChargeParticles(World world, Entity entity, float multiplier) {
        if (!world.isClient()) {
            float d = GildedGloryUtil.random(-0.02f, 0.02f);
            float e = GildedGloryUtil.random(0.5f, 1.0f) * multiplier;
            float f = GildedGloryUtil.random(-0.02f, 0.02f);

            GildedGloryUtil.sendChargingParticlePackets(world, entity, new Vector3f(0.5f, 0.75f, 0.83f), d, e, f);
        }
    }

    @Override
    public CustomAttackData onAttack(ItemStack stack, LivingEntity attacker, Entity target, DamageSource source, float amount) {
        if (ChargeableWeapon.getCharge(stack) == 0) {
            GildedGloryUtil.startLoopingSound(attacker.getWorld(), attacker, GildedGlory.id("iraedeus_hum"));
        }
        if (!attacker.getWorld().isClient()) {
            ChargeableWeapon.addCharge(stack, (int) (amount * 1.25f));
            IraedeusItem.setCooldown(stack, 80);
        }
        return new CustomAttackData(stack, attacker, target, source, amount, true);
    }

    public static void setCooldown(ItemStack stack, int cooldown) {
        NbtCompound tag = stack.getOrCreateNbt();

        if (cooldown > 0) tag.putInt("Cooldown", cooldown);
        else tag.remove("Cooldown");
    }

    public static int getCooldown(ItemStack stack) {
        return stack.getNbt() != null ? stack.getNbt().getInt("Cooldown") : 0;
    }

    @Override
    public Text getName(ItemStack stack) {
        return Text.translatable(this.getTranslationKey(stack)).setStyle(Style.EMPTY.withColor(0x4A626A));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("tooltip.gildedglory.iraedeus").formatted(Formatting.GRAY));
        tooltip.add(Text.translatable("tooltip.gildedglory.iraedeus_0",
                Text.keybind("keybind.gildedglory.iraedeus_return").setStyle(Style.EMPTY.withColor(0x4A626A))).formatted(Formatting.GRAY));
        tooltip.add(Text.translatable("tooltip.gildedglory.iraedeus_1",
                Text.keybind("keybind.gildedglory.iraedeus_target").setStyle(Style.EMPTY.withColor(0x4A626A))).formatted(Formatting.GRAY));
    }

    @Override
    public int getMaxCharge() {
        return 100;
    }

    @Override
    public boolean canLoseCharge(ItemStack stack) {
        return ChargeableWeapon.super.canLoseCharge(stack) && getCooldown(stack) == 0;
    }

    @Override
    public boolean offHandUsable() {
        return false;
    }

    @Override
    public boolean allowNbtUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        return !canLoseCharge(newStack) && !(getCooldown(newStack) > 0);
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return super.isItemBarVisible(stack) || ChargeableWeapon.hasCharge(stack);
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        return ChargeableWeapon.hasCharge(stack) ? 1661294 : super.getItemBarColor(stack);
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        return ChargeableWeapon.hasCharge(stack) ? Math.round(ChargeableWeapon.getCharge(stack) * 13.0f / this.getMaxCharge()) : super.getItemBarStep(stack);
    }

    @Override
    public SoundEvent getCritAttackSound(ItemStack stack) {
        float f = ChargeableWeapon.getChargePercentage(stack);

        if (f <= (float) 1 / 3) return ModSounds.IRAEDEUS_CRIT_LOW;
        else if (f <= (float) 2 / 3) return ModSounds.IRAEDEUS_CRIT_MEDIUM;
        else return ModSounds.IRAEDEUS_CRIT_HIGH;
    }

    @Override
    public SoundEvent getSweepAttackSound(ItemStack stack) {
        float f = ChargeableWeapon.getChargePercentage(stack);

        if (f <= (float) 1 / 3) return ModSounds.IRAEDEUS_SWEEP_LOW;
        else if (f <= (float) 2 / 3) return ModSounds.IRAEDEUS_SWEEP_MEDIUM;
        else return ModSounds.IRAEDEUS_SWEEP_HIGH;
    }

    @Override
    public SoundEvent getKnockbackAttackSound(ItemStack stack) {
        float f = ChargeableWeapon.getChargePercentage(stack);

        if (f <= (float) 1 / 3) return ModSounds.IRAEDEUS_KNOCKBACK_LOW;
        else if (f <= (float) 2 / 3) return ModSounds.IRAEDEUS_KNOCKBACK_MEDIUM;
        else return ModSounds.IRAEDEUS_KNOCKBACK_HIGH;
    }

    @Override
    public DefaultParticleType getSweepAttackParticle(ItemStack stack) {
        return ModParticles.IRAEDEUS_SLASH;
    }

    @Override
    public DefaultParticleType getCritAttackParticle(ItemStack stack) {
        return ModParticles.IRAEDEUS_VERTICAL_SLASH;
    }
}
