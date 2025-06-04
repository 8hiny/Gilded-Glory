package shiny.gildedglory.common.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.util.math.Vec3d;
import shiny.gildedglory.common.item.custom.ChargeableWeapon;

public interface DynamicSoundSource {

    Vec3d getPosition();

    default double getX() {
        return getPosition().x;
    }

    default double getY() {
        return getPosition().y;
    }

    default double getZ() {
        return getPosition().z;
    }

    default boolean isUsing(Item item) {
        return this instanceof LivingEntity entity && entity.getActiveItem().isOf(item);
    }

    default boolean isHolding(Item item, boolean offhand) {
        return this instanceof LivingEntity entity && (offhand ? entity.getOffHandStack().isOf(item) : entity.getMainHandStack().isOf(item));
    }

    default int getCharge() {
        if (this instanceof LivingEntity entity) {
            return ChargeableWeapon.getCharge(entity);
        }
        return 0;
    }

    boolean canPlay();

    default float getStress() {
        return 0.0f;
    }
}
