package shiny.gildedglory;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shiny.gildedglory.common.network.ModPackets;
import shiny.gildedglory.common.registry.block.ModBlocks;
import shiny.gildedglory.common.registry.block.entity.ModBlockEntities;
import shiny.gildedglory.common.registry.enchantment.ModEnchantments;
import shiny.gildedglory.common.registry.entity.ModEntities;
import shiny.gildedglory.common.registry.item.ModItemGroups;
import shiny.gildedglory.common.registry.item.ModItems;
import shiny.gildedglory.common.registry.particle.ModParticles;
import shiny.gildedglory.common.registry.sound.ModSounds;
import shiny.gildedglory.common.util.HeatedAnvilRecipeHandler;

import java.util.UUID;

public class GildedGlory implements ModInitializer {

	public static final String MOD_ID = "gildedglory";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final boolean FARMERS_DELIGHT_INSTALLED = FabricLoader.getInstance().isModLoaded("farmersdelight");

	//UUID'S for cosmetic particle spawning
	public static final UUID SHINY_UUID = UUID.fromString("a9bcfe9b-bb80-463d-848e-11e0b03f2b6e");

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

		HeatedAnvilRecipeHandler.addCompressionRecipe(ModItems.TWISTEEL_INGOT, new Item[] {Items.GOLD_INGOT, Items.COPPER_INGOT,  Items.IRON_INGOT, Items.NETHERITE_SCRAP});
		HeatedAnvilRecipeHandler.addCompressionRecipe(ModItems.FOOLS_GOLD_INGOT, new Item[] {Items.IRON_INGOT, Items.GLOWSTONE_DUST});
		HeatedAnvilRecipeHandler.addCompressionRecipe(ModItems.AURADEUS, new Item[] {Items.NETHERITE_SWORD, ModItems.TWISTEEL_INGOT,Items.GOLD_BLOCK});
		HeatedAnvilRecipeHandler.addCompressionRecipe(ModItems.TWISTEEL_SICKLE, new Item[] {Items.NETHERITE_HOE, ModItems.TWISTEEL_INGOT});
		HeatedAnvilRecipeHandler.addCompressionRecipe(ModItems.TWISTEEL_CHARM, new Item[] {ModItems.TWISTEEL_INGOT, ModItems.TWISTEEL_INGOT});
		HeatedAnvilRecipeHandler.addCompressionRecipe(ModItems.GILDED_HORN, new Item[] {Items.GOAT_HORN, ModItems.FOOLS_GOLD_INGOT, Items.NETHERITE_SCRAP});
	}

	public static Identifier id(String name) {
		return new Identifier(MOD_ID, name);
	}
}