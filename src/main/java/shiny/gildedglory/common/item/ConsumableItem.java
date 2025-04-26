package shiny.gildedglory.common.item;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.common.utility.TextUtils;

import java.util.List;

public class ConsumableItem extends Item {

    private final int maxUseTime;
    public final Item resultItem;

    public ConsumableItem(Settings settings, int maxUseTime, Item resultItem) {
        super(settings);

        this.maxUseTime = maxUseTime;
        this.resultItem = resultItem;
    }

    public ConsumableItem(Settings settings) {
        this(settings, 32, Items.AIR);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        super.finishUsing(stack, world, user);

        if (user instanceof ServerPlayerEntity serverPlayer) {
            Criteria.CONSUME_ITEM.trigger(serverPlayer, stack);
            serverPlayer.incrementStat(Stats.USED.getOrCreateStat(this));
        }

        if (stack.isEmpty()) {
            return new ItemStack(this.resultItem);
        }
        else {
            if (user instanceof PlayerEntity player && !player.getAbilities().creativeMode) {
                ItemStack itemStack = new ItemStack(this.resultItem);
                if (!player.getInventory().insertStack(itemStack)) {
                    player.dropItem(itemStack, false);
                }
            }
            return stack;
        }
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return this.maxUseTime;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return isDrink() ? UseAction.DRINK : UseAction.EAT;
    }

    @Override
    public SoundEvent getEatSound() {
        return this.isDrink() ? SoundEvents.INTENTIONALLY_EMPTY : SoundEvents.ENTITY_GENERIC_EAT;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        //This currently only works when farmer's delight is installed, if I add compat items from other mods I'll have to write this method myself
        if (!stack.getItem().getFoodComponent().getStatusEffects().isEmpty()) TextUtils.addFoodEffectTooltip(stack, tooltip, 1.0f);
    }

    public boolean isDrink() {
        return this.resultItem == Items.GLASS_BOTTLE;
    }
}
