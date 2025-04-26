package shiny.gildedglory.common.registry.component;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import shiny.gildedglory.GildedGlory;
import shiny.gildedglory.common.component.entity.ChainedComponent;
import shiny.gildedglory.common.component.entity.FoolsStatueComponent;
import shiny.gildedglory.common.component.entity.IraedeusComponent;

public class ModComponents implements EntityComponentInitializer {

    public static final ComponentKey<FoolsStatueComponent> FOOLS_STATUE = ComponentRegistry.getOrCreate(GildedGlory.id("fools_statue"), FoolsStatueComponent.class);
    public static final ComponentKey<ChainedComponent> CHAINED = ComponentRegistry.getOrCreate(GildedGlory.id("chained"), ChainedComponent.class);
    public static final ComponentKey<IraedeusComponent> IRAEDEUS = ComponentRegistry.getOrCreate(GildedGlory.id("iraedeus"), IraedeusComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.beginRegistration(LivingEntity.class, FOOLS_STATUE);
        registry.registerFor(LivingEntity.class, FOOLS_STATUE, FoolsStatueComponent::new);

        registry.beginRegistration(LivingEntity.class, CHAINED);
        registry.registerFor(LivingEntity.class, CHAINED, ChainedComponent::new);

        registry.beginRegistration(PlayerEntity.class, IRAEDEUS);
        registry.registerForPlayers(IRAEDEUS, IraedeusComponent::new, RespawnCopyStrategy.NEVER_COPY);
    }
}
