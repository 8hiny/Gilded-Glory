package shiny.gildedglory.common.registry.component;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;
import shiny.gildedglory.GildedGlory;
import shiny.gildedglory.common.component.entity.ChainedComponent;
import shiny.gildedglory.common.component.entity.FoolsStatueComponent;
import shiny.gildedglory.common.component.entity.IraedeusComponent;
import shiny.gildedglory.common.component.entity.ThrowableSwordComponent;

public class ModComponents implements EntityComponentInitializer {

    public static final ComponentKey<FoolsStatueComponent> FOOLS_STATUE = ComponentRegistry.getOrCreate(GildedGlory.id("fools_statue"), FoolsStatueComponent.class);
    public static final ComponentKey<ChainedComponent> CHAINED = ComponentRegistry.getOrCreate(GildedGlory.id("chained"), ChainedComponent.class);
    public static final ComponentKey<IraedeusComponent> IRAEDEUS = ComponentRegistry.getOrCreate(GildedGlory.id("iraedeus"), IraedeusComponent.class);
    public static final ComponentKey<ThrowableSwordComponent> THROWABLE_WIP = ComponentRegistry.getOrCreate(GildedGlory.id("throwable_wip"), ThrowableSwordComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.beginRegistration(LivingEntity.class, FOOLS_STATUE);
        registry.registerFor(LivingEntity.class, FOOLS_STATUE, FoolsStatueComponent::new);

        registry.beginRegistration(LivingEntity.class, CHAINED);
        registry.registerFor(LivingEntity.class, CHAINED, ChainedComponent::new);

        registry.registerFor(LivingEntity.class, IRAEDEUS, IraedeusComponent::new);

        registry.beginRegistration(PlayerEntity.class, THROWABLE_WIP);
        registry.registerForPlayers(THROWABLE_WIP, ThrowableSwordComponent::new, RespawnCopyStrategy.NEVER_COPY);
    }
}
