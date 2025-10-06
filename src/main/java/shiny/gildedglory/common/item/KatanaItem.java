package shiny.gildedglory.common.item;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import shiny.gildedglory.common.network.SlashedAreaPayload;
import shiny.gildedglory.common.registry.damage_type.ModDamageTypes;
import shiny.gildedglory.common.registry.item.ModItems;
import shiny.gildedglory.common.util.GildedGloryUtil;

public class KatanaItem extends SheathableSwordItem  {

    //TODO Add golden puddles
    //TODO Add different slash types
    //TODO Fix refractive post shader

    public KatanaItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (!user.isSneaking() && this.isSheathed(user, stack) && !this.sheathing) {
            if (!world.isClient()) {
                Vec3d pos = new Vec3d(user.getX(), user.getBodyY(0.5), user.getZ());
                GildedGloryUtil.sendPayloadToTracking(new SlashedAreaPayload(pos, 8.5f, 0.1f, 10, 20), world, user, null);

                this.areaAttack((ServerWorld) world, user, stack);
            }
            user.setCurrentHand(hand);
            return TypedActionResult.success(stack);
        }
        return super.use(world, user, hand);
    }

    public void areaAttack(ServerWorld world, PlayerEntity user, ItemStack stack) {
        for (Entity entity : world.getOtherEntities(null, Box.of(user.getPos(), 10, 10, 10))) {
            if (entity instanceof LivingEntity target && entity != user) {
                DamageSource source = new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(ModDamageTypes.SLASHED_AREA), user, user);
                float amount = EnchantmentHelper.getDamage(world, stack, target, source, (float) user.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE));

                entity.damage(source, amount);
                user.onAttacking(target);
                EnchantmentHelper.onTargetDamaged(world, target, source, stack);
            }
        }
    }

    @Override
    public Item getSheath(ItemStack stack) {
        return ModItems.KATANA_SHEATH;
    }
}
