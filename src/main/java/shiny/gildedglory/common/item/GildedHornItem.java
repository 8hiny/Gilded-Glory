package shiny.gildedglory.common.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import shiny.gildedglory.common.registry.sound.ModSounds;
import shiny.gildedglory.common.util.GildedGloryUtil;

import java.util.List;

public class GildedHornItem extends Item {

    public GildedHornItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        boolean success = false;

        List<Entity> entities = world.getOtherEntities(null, Box.of(user.getPos(), 16, 16, 16));
        if (user.getHealth() < user.getMaxHealth() || !entities.isEmpty()) {
            if (CharmItem.hasOwnedCharm(user)) {
                for (Entity entity : entities) {
                    if (entity instanceof PlayerEntity player && player.getHealth() < player.getMaxHealth() && CharmItem.hasOwnedCharm(player)) {
                        healEntity(player);
                        success = true;
                    }
                }
            }
            else {
                for (Entity entity : entities) {
                    if (entity instanceof LivingEntity livingEntity && livingEntity.getHealth() < livingEntity.getMaxHealth()) {
                        healEntity(livingEntity);
                        success = true;
                    }
                }
            }
        }

        if (success || (user.getHealth() >= user.getMaxHealth())) {
            world.playSound(null, user.getX(), user.getY(), user.getZ(), ModSounds.GILDED_HORN, user.getSoundCategory(), 1.0f, 1.0f);

            user.setCurrentHand(hand);
            user.getItemCooldownManager().set(this, success ? 600 : 140);
            user.incrementStat(Stats.USED.getOrCreateStat(this));
            return TypedActionResult.consume(itemStack);
        }
        return TypedActionResult.fail(itemStack);
    }

    public static void healEntity(LivingEntity entity) {
        entity.heal(6.0f);

        for (int i = 0; i < 7; i++) {
            double d = GildedGloryUtil.random(0.2f, 0.7f);
            double e = GildedGloryUtil.random(0.2f, 0.7f);
            double f = GildedGloryUtil.random(0.2f, 0.7f);
            entity.getWorld().addParticle(ParticleTypes.HEART, entity.getParticleX(1.0), entity.getRandomBodyY() + 0.5, entity.getParticleZ(1.0), d, e, f);
        }
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 140;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.TOOT_HORN;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("tooltip.gildedglory.gilded_horn").formatted(Formatting.GRAY));
    }
}
