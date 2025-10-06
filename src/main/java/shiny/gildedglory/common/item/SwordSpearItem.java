package shiny.gildedglory.common.item;

import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.Stats;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import shiny.gildedglory.GildedGlory;
import shiny.gildedglory.client.pose.ArmPose;
import shiny.gildedglory.client.pose.CustomArmPoses;
import shiny.gildedglory.common.item.custom.ChargeableWeapon;
import shiny.gildedglory.common.item.custom.CustomEffectsWeapon;
import shiny.gildedglory.common.item.custom.SprintUsableItem;
import shiny.gildedglory.common.registry.damage_type.ModDamageTypes;
import shiny.gildedglory.common.registry.enchantment.ModEnchantments;
import shiny.gildedglory.common.registry.particle.ModParticles;
import shiny.gildedglory.common.registry.sound.ModSounds;
import shiny.gildedglory.common.util.GildedGloryUtil;

import java.util.List;

public class SwordSpearItem extends SwordItem implements ChargeableWeapon, CustomEffectsWeapon, SprintUsableItem {

    public SwordSpearItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }

    public static AttributeModifiersComponent createAttributeModifiers(ToolMaterial material, float attackDamage, float attackSpeed, float extraRange) {
        return AttributeModifiersComponent.builder()
                .add(
                        EntityAttributes.GENERIC_ATTACK_DAMAGE,
                        new EntityAttributeModifier(
                                BASE_ATTACK_DAMAGE_MODIFIER_ID, attackDamage + material.getAttackDamage(), EntityAttributeModifier.Operation.ADD_VALUE
                        ),
                        AttributeModifierSlot.MAINHAND
                )
                .add(
                        EntityAttributes.GENERIC_ATTACK_SPEED,
                        new EntityAttributeModifier(BASE_ATTACK_SPEED_MODIFIER_ID, attackSpeed, EntityAttributeModifier.Operation.ADD_VALUE),
                        AttributeModifierSlot.MAINHAND
                )
                .add(
                        EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE,
                        new EntityAttributeModifier(
                                GildedGlory.id("base_attack_range"), extraRange, EntityAttributeModifier.Operation.ADD_VALUE
                        ),
                        AttributeModifierSlot.MAINHAND
                )
                .build();
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (user.getOffHandStack() == stack) {
            return TypedActionResult.fail(stack);
        }
        else {
            user.setCurrentHand(hand);
            GildedGloryUtil.playLoopingSound(world, user, GildedGlory.id("swordspear_charging"));
            return TypedActionResult.consume(stack);
        }
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (this.getMaxUseTime(stack, user) - remainingUseTicks == this.getMaxCharge()) {
            Vec3d vec3d = user.getRotationVector();
            world.addImportantParticle(ModParticles.ALERT, true, user.getX() + vec3d.x, user.getEyeY() + vec3d.y, user.getZ() + vec3d.z, 0, 0, 0);
        }
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        int charge = Math.min(this.getMaxUseTime(stack, user) - remainingUseTicks, this.getMaxCharge());
        int level = EnchantmentHelper.getLevel(world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).entryOf(ModEnchantments.SOLAR_FLARE), stack);
        if (level > 0) charge = (int) (charge * 0.5f) + 1;

        ChargeableWeapon.setCharge(stack, charge);

        if (user instanceof PlayerEntity player) {
            player.getItemCooldownManager().set(this, Math.max(40, charge * 2));
            player.incrementStat(Stats.USED.getOrCreateStat(this));
        }
        stack.damage(2, user, EquipmentSlot.MAINHAND);

        if (!world.isClient()) {
            float pitch = GildedGloryUtil.random(0.9f, 1.3f);
            world.playSound(null, user.getX(), user.getY(), user.getZ(), ModSounds.SWORDSPEAR_FIRE, SoundCategory.PLAYERS, 1.0f, pitch);
        }
        if (charge > 4) GildedGloryUtil.playLoopingSound(world, user, GildedGlory.id("swordspear_firing"));
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (entity instanceof LivingEntity user && !user.isUsingItem()) {
            int charge = ChargeableWeapon.getCharge(stack);
            if (selected && charge > 0) {
                boolean bl = EnchantmentHelper.getLevel(world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).entryOf(ModEnchantments.SOLAR_FLARE), stack) > 0;

                if (!world.isClient()) {
                    for (LivingEntity target : GildedGloryUtil.raycast(user, target -> target.isPartOfGame() && target.getRootVehicle() != user.getRootVehicle(), user.getRotationVec(1.0f), 0.3f, 34.0f, true)) {
                        DamageSource damageSource = new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(ModDamageTypes.BEAM), user);
                        float amount = bl ? 0.1f : 0.2f;
                        if (ChargeableWeapon.getChargePercentage(stack) < 0.5f) amount *= 2.0f;

                        target.damage(damageSource, amount);
                    }
                }
                else if (bl) {
                    Vec3d velocity = user.getRotationVec(1.0f).negate().multiply(-0.017 * charge + 1.35);
                    user.setVelocity(velocity);
                }
                ChargeableWeapon.tickCharge(stack);
            }
            else if (charge > 0 && !world.isClient()) {
                if (entity instanceof PlayerEntity player) {
                    int cooldown = charge;
                    if (player.getItemCooldownManager().isCoolingDown(this)) {
                        cooldown /= 2;
                    }
                    player.getItemCooldownManager().set(this, Math.max(40, cooldown));
                }
                ChargeableWeapon.setCharge(stack, 0);
            }
        }
    }

    @Override
    public Text getName(ItemStack stack) {
        return Text.translatable(this.getTranslationKey(stack)).setStyle(Style.EMPTY.withColor(0xCA9739));
    }

    @Override
    public boolean allowSprinting(PlayerEntity user, ItemStack stack) {
        return true;
    }

    @Override
    public boolean offHandUsable() {
        return false;
    }

    @Override
    public boolean chargeWhileUsing() {
        return true;
    }

    @Override
    public boolean chargeSetOnStoppedUsing() {
        return true;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 72000;
    }

    @Override
    public boolean isUsedOnRelease(ItemStack stack) {
        return stack.isOf(this);
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("tooltip.gildedglory.swordspear").formatted(Formatting.GRAY));
    }

    @Override
    public boolean allowComponentsUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        return !canLoseCharge(newStack);
    }

    @Override
    public int getMaxCharge() {
        return 100;
    }

    @Override
    public SoundEvent getCritAttackSound(ItemStack stack) {
        return ModSounds.SWORDSPEAR_CRIT;
    }

    @Override
    public SoundEvent getSweepAttackSound(ItemStack stack) {
        return ModSounds.SWORDSPEAR_SLASH;
    }

    @Override
    public SimpleParticleType getSweepAttackParticle(ItemStack stack) {
        return ModParticles.GOLD_SLASH;
    }

    @Override
    public SimpleParticleType getCritAttackParticle(ItemStack stack) {
        return ModParticles.GOLD_VERTICAL_SLASH;
    }

    @Override
    public ArmPose getMainHandPose(LivingEntity holder, ItemStack stack) {
        if (holder.getActiveItem() == stack) return CustomArmPoses.FORWARDS_BLOCKING;
        else if (ChargeableWeapon.hasCharge(stack)) return CustomArmPoses.FORWARDS_AIMING;
        return holder.getMainHandStack() == stack ? CustomArmPoses.TWO_HANDED_HOLDING : ArmPose.USE_VANILLA;
    }

    @Override
    public boolean hideOffHandItem(LivingEntity holder, ItemStack stack) {
        return true;
    }
}
