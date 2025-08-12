package shiny.gildedglory.common.item;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import shiny.gildedglory.common.item.custom.CustomAttackWeapon;
import shiny.gildedglory.common.item.custom.CustomEffectsWeapon;
import shiny.gildedglory.common.registry.damage_type.ModDamageTypes;
import shiny.gildedglory.common.registry.particle.ModParticles;
import shiny.gildedglory.common.registry.sound.ModSounds;
import shiny.gildedglory.common.util.GildedGloryUtil;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class SickleItem extends SwordItem implements CustomAttackWeapon, CustomEffectsWeapon {

    //TODO Rework or remove this

    protected static final Map<Block, Pair<Predicate<ItemUsageContext>, Consumer<ItemUsageContext>>> TILLING_ACTIONS = Maps.newHashMap(
            ImmutableMap.of(
                    Blocks.GRASS_BLOCK,
                    Pair.of(HoeItem::canTillFarmland, HoeItem.createTillAction(Blocks.FARMLAND.getDefaultState())),
                    Blocks.DIRT_PATH,
                    Pair.of(HoeItem::canTillFarmland, HoeItem.createTillAction(Blocks.FARMLAND.getDefaultState())),
                    Blocks.DIRT,
                    Pair.of(HoeItem::canTillFarmland, HoeItem.createTillAction(Blocks.FARMLAND.getDefaultState())),
                    Blocks.COARSE_DIRT,
                    Pair.of(HoeItem::canTillFarmland, HoeItem.createTillAction(Blocks.DIRT.getDefaultState())),
                    Blocks.ROOTED_DIRT,
                    Pair.of(itemUsageContext -> true, HoeItem.createTillAndDropAction(Blocks.DIRT.getDefaultState(), Items.HANGING_ROOTS))
            )
    );

    public SickleItem(ToolMaterial material, Settings settings) {
        super(material, settings.component(DataComponentTypes.TOOL, material.createComponent(BlockTags.HOE_MINEABLE)));
    }

    @Override
    public AttackContext onAttack(ItemStack stack, LivingEntity attacker, Entity target, DamageSource source, float amount) {
        boolean bl = Math.random() <= 0.2;

        if (bl) {
            float pitch = GildedGloryUtil.random(0.85f, 1.2f);
            attacker.getWorld().playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), ModSounds.SICKLE_CRIT, SoundCategory.PLAYERS, 0.8f, pitch);

            source = new DamageSource(attacker.getWorld().getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(ModDamageTypes.SICKLE_CRIT), attacker, attacker);
            amount *= 1.5f;
        }
        return new AttackContext(stack, attacker, target, source, amount, true);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();

        Pair<Predicate<ItemUsageContext>, Consumer<ItemUsageContext>> pair = TILLING_ACTIONS.get(
                world.getBlockState(blockPos).getBlock()
        );

        if (pair != null) {
            Predicate<ItemUsageContext> predicate = pair.getFirst();
            Consumer<ItemUsageContext> consumer = pair.getSecond();

            if (predicate.test(context)) {
                PlayerEntity player = context.getPlayer();

                if (player != null && player.isSneaking()) {
                    world.playSound(player, blockPos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0f, 1.0f);

                    if (!world.isClient()) {
                        consumer.accept(context);
                        context.getStack().damage(1, player, context.getHand() == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
                    }
                    return ActionResult.success(world.isClient());
                }
            }
        }
        return ActionResult.PASS;
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        if (!world.isClient() && state.getHardness(world, pos) != 0.0f) {
            stack.damage(1, miner, EquipmentSlot.MAINHAND);
        }
        return true;
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("tooltip.gildedglory.sickle").formatted(Formatting.GRAY));
    }

    @Override
    public SimpleParticleType getSweepAttackParticle(ItemStack stack) {
        return ModParticles.TWISTEEL_SLASH;
    }
}
