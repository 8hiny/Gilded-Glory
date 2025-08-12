package shiny.gildedglory.common.registry.data_component;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Uuids;
import shiny.gildedglory.GildedGlory;

import java.util.UUID;
import java.util.function.UnaryOperator;

public class ModComponentTypes {

    public static final ComponentType<Integer> CHARGE = register("charge", builder -> builder.codec(Codec.INT));
    public static final ComponentType<Integer> COOLDOWN = register("cooldown", builder -> builder.codec(Codec.INT));
    public static final ComponentType<UUID> OWNER_UUID = register("owner_uuid", builder -> builder.codec(Uuids.CODEC));
    public static final ComponentType<String> OWNER_NAME = register("owner_name", builder -> builder.codec(Codec.STRING));

    private static <T> ComponentType<T> register(String name, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, GildedGlory.id(name), ((ComponentType.Builder) builderOperator.apply(ComponentType.builder())).build());
    }

    public static void registerModComponentTypes() {
    }
}
