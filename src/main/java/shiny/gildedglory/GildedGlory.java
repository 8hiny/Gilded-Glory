package shiny.gildedglory;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shiny.gildedglory.common.command.TestRenderObjectCommand;
import shiny.gildedglory.common.network.ModNetworking;
import shiny.gildedglory.common.registry.ModRegistries;
import shiny.gildedglory.common.registry.block.ModBlocks;
import shiny.gildedglory.common.registry.block.entity.ModBlockEntities;
import shiny.gildedglory.common.registry.component.ModComponents;
import shiny.gildedglory.common.registry.data_component.ModComponentTypes;
import shiny.gildedglory.common.registry.entity.ModEntities;
import shiny.gildedglory.common.registry.item.ModItemGroups;
import shiny.gildedglory.common.registry.item.ModItems;
import shiny.gildedglory.common.registry.particle.ModParticles;
import shiny.gildedglory.common.registry.recipe.ModRecipeSerializers;
import shiny.gildedglory.common.registry.recipe.ModRecipeTypes;
import shiny.gildedglory.common.registry.sound.ModSounds;

public class GildedGlory implements ModInitializer {

	public static final String MOD_ID = "gildedglory";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final boolean FARMERS_DELIGHT_INSTALLED = FabricLoader.getInstance().isModLoaded("farmersdelight");

	@Override
	public void onInitialize() {
		ModRegistries.init();

		ModItems.registerModItems();
		ModItemGroups.registerModItemGroups();
		ModComponentTypes.registerModComponentTypes();

		ModBlocks.registerModBlocks();
		ModBlockEntities.registerModBlockEntities();

		ModEntities.registerModEntities();

		ModParticles.registerModParticles();
		ModSounds.registerModSounds();

		ModRecipeSerializers.registerModRecipeSerializers();
		ModRecipeTypes.registerModRecipeTypes();

		ModNetworking.registerModPayloads();
		ModNetworking.registerModServerReceivers();

		TestRenderObjectCommand.init();

		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			ModComponents.THROWABLE_WIP.get(handler.player).resetEntity();
			ModComponents.THROWABLE_WIP.get(handler.player).reset();
		});
	}

	public static Identifier id(String name) {
		return Identifier.of(MOD_ID, name);
	}
}