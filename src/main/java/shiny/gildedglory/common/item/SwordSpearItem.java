package shiny.gildedglory.common.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.Stats;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import shiny.gildedglory.GildedGlory;
import shiny.gildedglory.common.registry.damage_type.ModDamageTypes;
import shiny.gildedglory.common.registry.enchantment.ModEnchantments;
import shiny.gildedglory.common.registry.particle.ModParticles;
import shiny.gildedglory.common.registry.sound.ModSounds;
import shiny.gildedglory.common.util.GildedGloryUtil;

import java.util.List;
import java.util.UUID;

public class SwordSpearItem extends SwordItem implements ChargeableWeapon, CustomEffectsWeapon, SprintUsableItem {

    //To-Do:
    //Add a custom crit particle
    //(Add a golden screen overlay which is applied while firing)

    private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;
    private final float attackDamage;

    public SwordSpearItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);

        this.attackDamage = attackDamage + toolMaterial.getAttackDamage();
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(
                ReachEntityAttributes.ATTACK_RANGE,
                new EntityAttributeModifier(UUID.fromString("e7f37295-a925-4b70-ba00-0e25f60ea8f9"), "Weapon modifier", 0.75, EntityAttributeModifier.Operation.ADDITION)
        );
        builder.put(
                EntityAttributes.GENERIC_ATTACK_DAMAGE,
                new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", this.attackDamage, EntityAttributeModifier.Operation.ADDITION)
        );
        builder.put(
                EntityAttributes.GENERIC_ATTACK_SPEED,
                new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", attackSpeed, EntityAttributeModifier.Operation.ADDITION)
        );
        this.attributeModifiers = builder.build();
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (user.getOffHandStack() == stack) {
            return TypedActionResult.fail(stack);
        }
        else {
            user.setCurrentHand(hand);
            GildedGloryUtil.startLoopingSound(world, user, GildedGlory.id("swordspear_charging"));
            return TypedActionResult.consume(stack);
        }
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (this.getMaxUseTime(stack) - remainingUseTicks == this.getMaxCharge()) {
            Vec3d vec3d = user.getRotationVector();
            world.addImportantParticle(ModParticles.ALERT, true, user.getX() + vec3d.x, user.getEyeY() + vec3d.y, user.getZ() + vec3d.z, 0, 0, 0);
        }
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        int charge = this.getMaxUseTime(stack) - remainingUseTicks;
        int level = EnchantmentHelper.getLevel(ModEnchantments.SOLAR_FLARE, stack);
        if (level > 0) charge = (int) (charge * 0.5f) + 1;

        ChargeableWeapon.setCharge(stack, charge);

        if (user instanceof PlayerEntity player) {
            player.getItemCooldownManager().set(this, Math.max(40, charge * 2));
            player.incrementStat(Stats.USED.getOrCreateStat(this));
        }
        stack.damage(2, user, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));

        if (!world.isClient()) {
            float pitch = GildedGloryUtil.random(0.9f, 1.3f);
            world.playSound(null, user.getX(), user.getY(), user.getZ(), ModSounds.SWORDSPEAR_FIRE, SoundCategory.PLAYERS, 1.0f, pitch);
        }
        if (charge > 4) GildedGloryUtil.startLoopingSound(world, user, GildedGlory.id("swordspear_firing"));
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (entity instanceof LivingEntity user && !user.isUsingItem()) {
            int charge = ChargeableWeapon.getCharge(stack);
            if (selected && charge > 0) {
                boolean bl = EnchantmentHelper.getLevel(ModEnchantments.SOLAR_FLARE, stack) > 0;

                if (!world.isClient()) {
                    for (LivingEntity target : GildedGloryUtil.raycast(user, target -> target.isPartOfGame() && target.getRootVehicle() != user.getRootVehicle(), user.getRotationVec(1.0f), 0.35f, 34.0f, true)) {
                        DamageSource damageSource = new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(ModDamageTypes.BEAM), user);
                        target.damage(damageSource, bl ? 0.1f : 0.2f);
                    }
                }
                else if (bl) {
                    Vec3d velocity = user.getRotationVec(1.0f).negate().multiply(-0.017 * charge + 1.35);
                    user.setVelocity(velocity);
                }
                ChargeableWeapon.tickCharge(stack);
            }
            else if (charge > 0) {
                if (entity instanceof PlayerEntity player && !player.getItemCooldownManager().isCoolingDown(this)) {
                    player.getItemCooldownManager().set(this, Math.max(40, charge));
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
    public boolean offHandUsable() {
        return false;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }

    @Override
    public boolean isUsedOnRelease(ItemStack stack) {
        return stack.isOf(this);
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? this.attributeModifiers : super.getAttributeModifiers(slot);
    }

    @Override
    public float getAttackDamage() {
        return this.attackDamage;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("tooltip.gildedglory.swordspear").formatted(Formatting.GRAY));
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
    public SoundEvent getKnockbackAttackSound(ItemStack stack) {
        return ModSounds.SWORDSPEAR_CRIT;
    }

    @Override
    public BipedEntityModel.ArmPose getMainHandPose() {
        return BipedEntityModel.ArmPose.BLOCK;
    }

    @Override
    public BipedEntityModel.ArmPose getOffHandPose() {
        return BipedEntityModel.ArmPose.CROSSBOW_CHARGE;
    }

    @Override
    public boolean isTwoHanded() {
        return true;
    }

    @Override
    public boolean overrideHandPoses(LivingEntity holder, ItemStack stack) {
        return this.canLoseCharge(stack) || holder.getActiveItem() == stack;
    }

    @Override
    public boolean allowNbtUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        return !canLoseCharge(newStack);
    }
}
