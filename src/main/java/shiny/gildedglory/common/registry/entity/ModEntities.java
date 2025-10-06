package shiny.gildedglory.common.registry.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import shiny.gildedglory.GildedGlory;
import shiny.gildedglory.common.entity.ThrowableSwordEntity;
import shiny.gildedglory.common.entity.SlashProjectileEntity;

public class ModEntities {

    public static EntityType<SlashProjectileEntity> SLASH_PROJECTILE;
    public static EntityType<ThrowableSwordEntity> IRAEDEUS;

    public static void registerModEntities() {
        SLASH_PROJECTILE = Registry.register(
                Registries.ENTITY_TYPE,
                GildedGlory.id("slash"),
                EntityType.Builder.<SlashProjectileEntity>create(SlashProjectileEntity::new, SpawnGroup.MISC)
                        .dimensions(2.4f, 0.5f)
                        .disableSaving()
                        .makeFireImmune()
                        .build()
        );
        IRAEDEUS = Registry.register(
                Registries.ENTITY_TYPE,
                GildedGlory.id("iraedeus"),
                EntityType.Builder.<ThrowableSwordEntity>create(ThrowableSwordEntity::new, SpawnGroup.MISC)
                        .dimensions(1.0f, 0.5f)
                        .makeFireImmune()
                        .build()
        );
    }
}
