package shiny.gildedglory.common.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
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
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import shiny.gildedglory.client.pose.ArmPose;
import shiny.gildedglory.client.pose.CustomArmPoses;
import shiny.gildedglory.common.item.custom.CustomEffectsWeapon;
import shiny.gildedglory.common.item.custom.SprintUsableItem;
import shiny.gildedglory.common.network.SlashedAreaS2CPacket;
import shiny.gildedglory.common.registry.damage_type.ModDamageTypes;
import shiny.gildedglory.common.util.GildedGloryUtil;

import java.util.UUID;

public class KatanaItem extends SwordItem implements CustomEffectsWeapon, SprintUsableItem {

    //TODO Make sheathing mechanic (with sheathing and unsheathing sound & particles)
    //TODO Make sheathing animation for first & third person
    //TODO Add golden puddles
    //TODO Add different slash types
    //TODO Fix refractive post shader

    private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;
    private final float attackDamage;

    public KatanaItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);

        this.attackDamage = attackDamage + toolMaterial.getAttackDamage();
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(
                EntityAttributes.GENERIC_ATTACK_DAMAGE,
                new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", this.attackDamage, EntityAttributeModifier.Operation.ADDITION)
        );
        builder.put(
                EntityAttributes.GENERIC_ATTACK_SPEED,
                new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", attackSpeed, EntityAttributeModifier.Operation.ADDITION)
        );
        builder.put(
                ReachEntityAttributes.ATTACK_RANGE,
                new EntityAttributeModifier(UUID.fromString("e7f37295-a925-4b70-ba00-0e25f60ea8f9"), "Weapon modifier", 0.5, EntityAttributeModifier.Operation.ADDITION)
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
            if (!world.isClient()) {
                Vec3d pos = new Vec3d(user.getX(), user.getBodyY(0.5), user.getZ());
                SlashedAreaS2CPacket packet = new SlashedAreaS2CPacket(pos, 8.5f, 0.1f, 10, 20);
                GildedGloryUtil.sendPackets(packet, world, user, null);

                this.areaAttack(world, user, stack);
            }
            user.setCurrentHand(hand);
            return TypedActionResult.consume(stack);
        }
    }

    public void areaAttack(World world, PlayerEntity user, ItemStack stack) {
        for (Entity entity : world.getOtherEntities(null, Box.of(user.getPos(), 10, 10, 10))) {
            if (entity instanceof LivingEntity target && entity != user) {
                float amount = (float) user.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
                amount += EnchantmentHelper.getAttackDamage(stack, target.getGroup());

                DamageSource source = new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(ModDamageTypes.SLASHED_AREA), user, user);
                entity.damage(source, amount);

                int i = EnchantmentHelper.getFireAspect(user);
                if (i > 0 && !target.isOnFire()) target.setOnFireFor(i * 4);

                user.onAttacking(target);
                EnchantmentHelper.onTargetDamaged(user, target);
                EnchantmentHelper.onUserDamaged(target, user);
            }
        }
    }

    @Override
    public float getAttackDamage() {
        return this.attackDamage;
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? this.attributeModifiers : super.getAttributeModifiers(slot);
    }

    @Override
    public ArmPose getMainHandPose(LivingEntity holder, ItemStack stack) {
        if (holder.getOffHandStack() == stack && holder.getMainHandStack().isEmpty()) return CustomArmPoses.BACKWARDS_HOLDING;
        return CustomEffectsWeapon.super.getMainHandPose(holder, stack);
    }

    @Override
    public ArmPose getOffHandPose(LivingEntity holder, ItemStack stack) {
        if (holder.getOffHandStack() == stack && holder.getMainHandStack().isEmpty()) return CustomArmPoses.BACKWARDS_HOLDING;
        return CustomEffectsWeapon.super.getOffHandPose(holder, stack);
    }
}
