package shiny.gildedglory.common.item;

import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import shiny.gildedglory.GildedGlory;
import shiny.gildedglory.client.pose.ArmPose;
import shiny.gildedglory.client.pose.CustomArmPoses;
import shiny.gildedglory.client.use_action.BaseUseAction;
import shiny.gildedglory.client.use_action.CustomUseActions;
import shiny.gildedglory.common.item.custom.CustomEffectsWeapon;
import shiny.gildedglory.common.item.custom.SheathableWeapon;
import shiny.gildedglory.common.registry.data_component.ModComponentTypes;
import shiny.gildedglory.common.registry.sound.ModSounds;
import shiny.gildedglory.common.util.GildedGloryUtil;

public class SheathableSwordItem extends SwordItem implements CustomEffectsWeapon, SheathableWeapon {

    public SheathableSwordItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }

    public static AttributeModifiersComponent createAttributeModifiers(ToolMaterial material, float attackDamage, float attackSpeed, float sweepingDamage) {
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
                        EntityAttributes.PLAYER_SWEEPING_DAMAGE_RATIO,
                        new EntityAttributeModifier(
                                GildedGlory.id("base_additional_sweeping_damage"), sweepingDamage, EntityAttributeModifier.Operation.ADD_VALUE
                        ),
                        AttributeModifierSlot.MAINHAND
                )
                .build();
    }

    //Need a more reliable system to detect whether the player just finished sheathing the weapon and is still holding right click

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (user.getOffHandStack() == stack) {
            return super.use(world, user, hand);
        }
        else {
            if (!this.isSheathed(user, stack)) {
                world.playSound(user, user.getX(), user.getY(), user.getZ(), ModSounds.SHEATH_WEAPON, user.getSoundCategory(), 1.0f, 1.0f);
                GildedGloryUtil.startPlayerAnimation(world, user, GildedGlory.id("sheathing"));
            }

            user.setCurrentHand(hand);
            return TypedActionResult.consume(stack);
        }
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        int i = this.getMaxUseTime(stack, user) - remainingUseTicks;
        if (i == this.sheathTime() - 1) {
            SheathableWeapon.setSheathed(stack, true);
        }
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!this.isSheathed(user, stack)) {
            if (user instanceof PlayerEntity player) {
                GildedGloryUtil.startPlayerAnimation(world, player, null);
            }
        }
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 72000;
    }

    @Override
    public boolean currentlySheathing(LivingEntity holder, ItemStack stack) {
        return holder.isUsing(this) && holder.getItemUseTime() <= this.sheathTime() + 4;
    }

    @Override
    public boolean isSheathed(LivingEntity holder, ItemStack stack) {
        return stack.getOrDefault(ModComponentTypes.SHEATHED, false);
    }

    @Override
    public int sheathTime() {
        return 10;
    }

    @Override
    public Item getSheath(ItemStack stack) {
        return Items.AIR;
    }

    @Override
    public SoundEvent getSheathSound(ItemStack stack) {
        return ModSounds.SHEATH_WEAPON;
    }

    @Override
    public ArmPose getMainHandPose(LivingEntity holder, ItemStack stack) {
        if (isSheathed(holder, stack)) {
            if (holder.getOffHandStack() == stack && holder.getMainHandStack().isEmpty()) return CustomArmPoses.BACKWARDS_HOLDING_LEFT;
            else if (holder.getMainHandStack() == stack) return CustomArmPoses.BACKWARDS_HOLDING_RIGHT;
        }
        return CustomEffectsWeapon.super.getMainHandPose(holder, stack);
    }

    @Override
    public BaseUseAction getCustomUseAction(LivingEntity holder, ItemStack stack) {
        if (!this.isSheathed(holder, stack)) {
            return CustomUseActions.SHEATH;
        }
        return CustomEffectsWeapon.super.getCustomUseAction(holder, stack);
    }
}
