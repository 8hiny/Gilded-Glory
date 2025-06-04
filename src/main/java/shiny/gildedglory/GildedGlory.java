package shiny.gildedglory;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shiny.gildedglory.client.pose.CustomArmPoses;
import shiny.gildedglory.client.pose.CustomArmPose;
import shiny.gildedglory.common.command.TestRenderObjectCommand;
import shiny.gildedglory.common.network.ModPackets;
import shiny.gildedglory.common.registry.block.ModBlocks;
import shiny.gildedglory.common.registry.block.entity.ModBlockEntities;
import shiny.gildedglory.common.registry.component.ModComponents;
import shiny.gildedglory.common.registry.enchantment.ModEnchantments;
import shiny.gildedglory.common.registry.entity.ModEntities;
import shiny.gildedglory.common.registry.item.ModItemGroups;
import shiny.gildedglory.common.registry.item.ModItems;
import shiny.gildedglory.common.registry.particle.ModParticles;
import shiny.gildedglory.common.registry.recipe.ModRecipeTypes;
import shiny.gildedglory.common.registry.recipe.ModRecipes;
import shiny.gildedglory.common.registry.sound.ModSounds;

public class GildedGlory implements ModInitializer {

	public static final String MOD_ID = "gildedglory";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final boolean FARMERS_DELIGHT_INSTALLED = FabricLoader.getInstance().isModLoaded("farmersdelight");

	//Custom Registries
	public static final RegistryKey<Registry<CustomArmPose>> registryKey = RegistryKey.ofRegistry(id("custom_arm_pose"));
	public static final Registry<CustomArmPose> CUSTOM_ARM_POSE = FabricRegistryBuilder.createSimple(registryKey)
			.attribute(RegistryAttribute.SYNCED)
			.buildAndRegister();

	@Override
	public void onInitialize() {
		ModItems.registerModItems();
		ModItemGroups.registerModItemGroups();
		ModBlocks.registerModBlocks();
		ModBlockEntities.registerModBlockEntities();
		ModEntities.registerModEntities();
		ModEnchantments.registerModEnchantments();
		ModParticles.registerModParticles();
		ModSounds.registerModSounds();
		ModPackets.registerModPackets();
		ModRecipes.registerModRecipes();
		ModRecipeTypes.registerModRecipeTypes();
		CustomArmPoses.registerCustomArmPoses();

		TestRenderObjectCommand.init();

		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			ModComponents.IRAEDEUS.get(handler.player).resetEntity();
			ModComponents.IRAEDEUS.get(handler.player).reset();
		});
	}

	public static Identifier id(String name) {
		return new Identifier(MOD_ID, name);
	}
}