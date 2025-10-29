package shiny.gildedglory.common.item;

import net.minecraft.block.*;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.ToolComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.Stats;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import shiny.gildedglory.GildedGlory;
import shiny.gildedglory.client.pose.ArmPose;
import shiny.gildedglory.client.pose.CustomArmPoses;
import shiny.gildedglory.client.use_action.BaseUseAction;
import shiny.gildedglory.client.use_action.CustomUseActions;
import shiny.gildedglory.common.component.entity.ChainedComponent;
import shiny.gildedglory.common.item.custom.CustomAttackWeapon;
import shiny.gildedglory.common.item.custom.CustomEffectsWeapon;
import shiny.gildedglory.common.item.custom.SprintUsableItem;
import shiny.gildedglory.common.registry.component.ModComponents;
import shiny.gildedglory.common.registry.enchantment.ModEnchantments;
import shiny.gildedglory.common.registry.entity.ModEntities;
import shiny.gildedglory.common.registry.particle.ModParticles;
import shiny.gildedglory.common.util.GildedGloryUtil;
import shiny.gildedglory.common.entity.SlashProjectileEntity;
import shiny.gildedglory.common.registry.sound.ModSounds;

import java.util.List;

public class AuradeusItem extends AxeItem implements CustomAttackWeapon, CustomEffectsWeapon, SprintUsableItem {

    //TODO Make the Chained overlay less obtrusive (decrease the size of the animated chains)
    //TODO Add additional crit sound effect
    //TODO Redo gold and twisteel slash particles (more contrast, less colors)
    //TODO Redo slash model (manual item model in bb)
    //TODO Fix the Chained overlay being faster in singleplayer or when you're the lan server host (main player)
    //TODO Make this much cooler: Improve visuals, add new abilities; Make the trio of main Shiny weapons each an ultimate powerhouse with a different combat feel (plus major aura)

    //New Enchantment: Malevolent Kitchen
    //Charge time is increased threefold
    //Horizontal slashes travel as quickly as vertical slashes and are less visible (use three pulsing particles with less alpha)
    //Vertical slashes have a base damage value of 1, which is multiplied by the target's armor value x0.8
    //Receiving damage from an attacker while charging a slash stops charging it, negates some percentage of the damage, and builds up charge
    //Once enough charge is stored, firing a vertical instead slash knocks the user backwards and fires a fireball made from hellfire, which creates hellfire in a large area
    //Hellfire is not extinguished by water, instead remaining for a set duration

    public AuradeusItem(ToolMaterial material, Settings settings) {
        super(material, settings.component(DataComponentTypes.TOOL, createToolComponent()));
    }

    //TODO Fix this
    private static ToolComponent createToolComponent() {
        return new ToolComponent(
                List.of(
                        ToolComponent.Rule.ofAlwaysDropping(List.of(Blocks.COBWEB), 15.0f),
                        ToolComponent.Rule.of(BlockTags.SWORD_EFFICIENT, 10.0f),
                        ToolComponent.Rule.of(BlockTags.AXE_MINEABLE, 10.0f)
                ), 1.0f, 1
        );
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
            return super.use(world, user, hand);
        }
        else {
            user.setCurrentHand(hand);
            GildedGloryUtil.playLoopingSound(world, user, GildedGlory.id("auradeus_hum"));
            return TypedActionResult.consume(stack);
        }
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        boolean bl = this.getMaxUseTime(stack, user) - remainingUseTicks > 15;
        boolean bl1 = EnchantmentHelper.getLevel(world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).entryOf(ModEnchantments.ENMA), stack) > 0;

        if (!world.isClient()) {
            Vec3d pos = GildedGloryUtil.getThrowPos(user, ModEntities.SLASH_PROJECTILE);
            SlashProjectileEntity slashEntity = new SlashProjectileEntity(world, user, pos.x, pos.y, pos.z, bl, bl1);
            slashEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0f, bl ? 3.0f : 2.3f, 0.0f);
            slashEntity.setItem(stack);
            world.spawnEntity(slashEntity);

            float pitch = GildedGloryUtil.random(0.9f, 1.1f);
            world.playSound(null, user.getX(), user.getY(), user.getZ(), ModSounds.AURADEUS_SLASH_PROJECTILE, SoundCategory.PLAYERS, 1.0f, bl ? pitch : pitch + 0.5f);
        }

        if (user instanceof PlayerEntity playerEntity) {
            playerEntity.getItemCooldownManager().set(this, bl ? 20 : 10);
            playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
        }

        stack.damage(1, user, EquipmentSlot.MAINHAND);
        user.swingHand(user.getActiveHand());
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (this.getMaxUseTime(stack, user) - remainingUseTicks == 15) {
            Vec3d vec3d = user.getRotationVector();
            world.addImportantParticle(ModParticles.ALERT, true, user.getX() + vec3d.x, user.getEyeY() + vec3d.y, user.getZ() + vec3d.z, 0, 0, 0);
        }
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.damage(1, attacker, EquipmentSlot.MAINHAND);
        return true;
    }

    @Override
    public AttackContext onAttack(ItemStack stack, LivingEntity attacker, Entity target, DamageSource source, float amount, boolean critical, boolean sweeping) {
        if (EnchantmentHelper.getLevel(attacker.getRegistryManager().get(RegistryKeys.ENCHANTMENT).entryOf(ModEnchantments.ENMA), stack) > 0 && target instanceof LivingEntity livingEntity) {
            ChainedComponent attackerComponent = ModComponents.CHAINED.get(attacker);
            ChainedComponent targetComponent = ModComponents.CHAINED.get(target);

            if (attackerComponent.getDuration() == 0 && targetComponent.getDuration() == 0 && target.isAlive()) {
                targetComponent.addProgress(attacker, (int) (amount * 2.0f));

                if (targetComponent.getDuration() > 0) {
                    attackerComponent.chain(livingEntity, targetComponent.getDuration());
                    attackerComponent.setAttacker(true);
                    targetComponent.setAttacker(false);
                }
            }
        }
        return new AttackContext(stack, attacker, target, source, amount, true);
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
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return !miner.isCreative();
    }

    @Override
    public boolean allowSprinting(PlayerEntity user, ItemStack stack) {
        return true;
    }

    @Override
    public Text getName(ItemStack stack) {
        return Text.translatable(this.getTranslationKey(stack)).setStyle(Style.EMPTY.withColor(0xCC495C));
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("tooltip.gildedglory.auradeus").formatted(Formatting.GRAY));
    }

    @Override
    public SimpleParticleType getAttackParticle(ItemStack stack) {
        if (Math.random() <= 0.5) return ModParticles.TWISTEEL_SLASH;
        return ModParticles.GOLD_SLASH;
    }

    @Override
    public SimpleParticleType getCritAttackParticle(ItemStack stack) {
        if (Math.random() <= 0.5) return ModParticles.TWISTEEL_VERTICAL_SLASH;
        return ModParticles.GOLD_VERTICAL_SLASH;
    }

    @Override
    public SoundEvent getDefaultAttackSound(ItemStack stack) {
        return ModSounds.AURADEUS_SLASH;
    }

    @Override
    public ArmPose getMainHandPose(LivingEntity holder, ItemStack stack) {
        return holder.getActiveItem() == stack ? CustomArmPoses.SIDEWAYS_CHARGING : CustomEffectsWeapon.super.getMainHandPose(holder, stack);
    }

    @Override
    public BaseUseAction getCustomUseAction(LivingEntity holder, ItemStack stack) {
        return CustomUseActions.AURADEUS;
    }

    @Override
    public boolean hideOffHandItem(LivingEntity holder, ItemStack stack) {
        return holder.isUsing(this);
    }
}
