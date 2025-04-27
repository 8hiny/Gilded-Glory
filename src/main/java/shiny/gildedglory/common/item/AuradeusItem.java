package shiny.gildedglory.common.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import net.minecraft.block.*;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.particle.DefaultParticleType;
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
import org.jetbrains.annotations.Nullable;
import shiny.gildedglory.GildedGlory;
import shiny.gildedglory.common.component.entity.ChainedComponent;
import shiny.gildedglory.common.registry.component.ModComponents;
import shiny.gildedglory.common.registry.enchantment.ModEnchantments;
import shiny.gildedglory.common.registry.particle.ModParticles;
import shiny.gildedglory.common.util.GildedGloryUtil;
import shiny.gildedglory.common.entity.SlashProjectileEntity;
import shiny.gildedglory.common.registry.sound.ModSounds;

import java.util.List;
import java.util.UUID;

public class AuradeusItem extends AxeItem implements CustomAttackWeapon, CustomEffectsWeapon, SprintUsableItem {

    private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;
    protected final float miningSpeed;
    private final float attackDamage;

    public AuradeusItem(ToolMaterial toolMaterial, float attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);

        this.attackDamage = attackDamage + toolMaterial.getAttackDamage();
        this.miningSpeed = toolMaterial.getMiningSpeedMultiplier();
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(
                ReachEntityAttributes.ATTACK_RANGE,
                new EntityAttributeModifier(UUID.fromString("e7f37295-a925-4b70-ba00-0e25f60ea8f9"), "Weapon modifier", 0.5, EntityAttributeModifier.Operation.ADDITION)
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
            GildedGloryUtil.startLoopingSound(world, user, GildedGlory.id("auradeus_hum"));
            return TypedActionResult.consume(stack);
        }
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        boolean bl = this.getMaxUseTime(stack) - remainingUseTicks > 15;

        float f = (float) user.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) + 1.5f;
        float g = EnchantmentHelper.getAttackDamage(stack, user.getGroup());
        f += g;

        if (!world.isClient()) {
            SlashProjectileEntity slashEntity = new SlashProjectileEntity(world, user, user.getX(), user.getEyeY() - 0.3f, user.getZ(), f, bl, EnchantmentHelper.getLevel(ModEnchantments.CHAINED, stack) > 0);
            slashEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0f, bl ? 3.0f : 2.3f, 0.0f);
            world.spawnEntity(slashEntity);

            float pitch = GildedGloryUtil.random(0.9f, 1.1f);
            world.playSound(null, user.getX(), user.getY(), user.getZ(), ModSounds.AURADEUS_SLASH_PROJECTILE, SoundCategory.PLAYERS, 1.0f, bl ? pitch : pitch + 0.5f);
        }

        if (user instanceof PlayerEntity playerEntity) {
            playerEntity.getItemCooldownManager().set(this, bl ? 20 : 10);
            playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
        }

        stack.damage(1, user, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        user.swingHand(user.getActiveHand());
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (this.getMaxUseTime(stack) - remainingUseTicks == 15) {
            Vec3d vec3d = user.getRotationVector();
            world.addImportantParticle(ModParticles.ALERT, true, user.getX() + vec3d.x, user.getEyeY() + vec3d.y, user.getZ() + vec3d.z, 0, 0, 0);
        }
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.damage(1, attacker, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        return true;
    }

    @Override
    public CustomAttackData onAttack(ItemStack stack, LivingEntity attacker, Entity target, DamageSource source, float amount) {
        if (EnchantmentHelper.getLevel(ModEnchantments.CHAINED, stack) > 0 && target instanceof LivingEntity livingEntity) {
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
        return new CustomAttackData(stack, attacker, target, source, amount, true);
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
    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
        if (state.isOf(Blocks.COBWEB)) {
            return 15.0f;
        }
        else if (state.isIn(BlockTags.SWORD_EFFICIENT) || state.isIn(BlockTags.AXE_MINEABLE)) {
            return 10.0f;
        }
        return 1.0f;
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return !miner.isCreative();
    }

    @Override
    public boolean isSuitableFor(BlockState state) {
        return state.isOf(Blocks.COBWEB) || state.isIn(BlockTags.SWORD_EFFICIENT) || state.isIn(BlockTags.AXE_MINEABLE);
    }

    @Override
    public Text getName(ItemStack stack) {
        return Text.translatable(this.getTranslationKey(stack)).setStyle(Style.EMPTY.withColor(0xCC495C));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("tooltip.gildedglory.auradeus").formatted(Formatting.GRAY));
    }

    @Override
    public float getAttackDamage() {
        return this.attackDamage;
    }

    @Override
    public DefaultParticleType getAttackParticle() {
        if (Math.random() <= 0.5) return ModParticles.TWISTEEL_SLASH;
        return ModParticles.GOLD_SLASH;
    }

    @Override
    public DefaultParticleType getCritAttackParticle() {
        if (Math.random() <= 0.5) return ModParticles.TWISTEEL_VERTICAL_SLASH;
        return ModParticles.GOLD_VERTICAL_SLASH;
    }

    @Override
    public SoundEvent getDefaultAttackSound(ItemStack stack) {
        return ModSounds.AURADEUS_SLASH;
    }
}
