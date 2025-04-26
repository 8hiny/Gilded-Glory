package shiny.gildedglory.common.registry.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import shiny.gildedglory.GildedGlory;
import shiny.gildedglory.common.entity.IraedeusEntity;
import shiny.gildedglory.common.entity.SlashProjectileEntity;

public class ModEntities {

    public static EntityType<SlashProjectileEntity> SLASH_PROJECTILE;
    public static EntityType<IraedeusEntity> IRAEDEUS;

    public static void registerModEntities() {
        SLASH_PROJECTILE = Registry.register(
                Registries.ENTITY_TYPE,
                GildedGlory.id("slash"),
                FabricEntityTypeBuilder.<SlashProjectileEntity>create(SpawnGroup.MISC, SlashProjectileEntity::new)
                        .dimensions(EntityDimensions.fixed(2.4f, 0.5f))
                        .disableSaving()
                        .fireImmune()
                        .build()
        );
        IRAEDEUS = Registry.register(
                Registries.ENTITY_TYPE,
                GildedGlory.id("iraedeus"),
                FabricEntityTypeBuilder.<IraedeusEntity>create(SpawnGroup.MISC, IraedeusEntity::new)
                        .dimensions(EntityDimensions.fixed(1.0f, 0.5f))
                        .fireImmune()
                        .build()
        );
    }
}
