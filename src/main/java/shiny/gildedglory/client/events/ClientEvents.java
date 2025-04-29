package shiny.gildedglory.client.events;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.random.Random;
import shiny.gildedglory.GildedGlory;
import shiny.gildedglory.client.util.GildedGloryUtil;
import shiny.gildedglory.common.component.entity.IraedeusComponent;
import shiny.gildedglory.common.registry.component.ModComponents;
import shiny.gildedglory.common.registry.particle.ModParticles;
import shiny.gildedglory.common.util.DynamicSoundManager;

public class ClientEvents {

    public static void clientTick(MinecraftClient client) {
        DynamicSoundManager.getInstance().tick();
        IraedeusComponent.clientTick(client);
        addCosmeticPlayerParticles(client);
    }

    public static void addCosmeticPlayerParticles(MinecraftClient client) {
        ClientWorld world = client.world;
        if (world != null) {
            PlayerEntity shiny = world.getPlayerByUuid(GildedGlory.SHINY_UUID);
            Random random = Random.create();

            double offsetX = random.nextGaussian() * 0.35;
            double offsetY = random.nextGaussian() * 0.4;
            double offsetZ = random.nextGaussian() * 0.35;

            if (Math.random() < 0.175 && shiny != null) {
                GildedGloryUtil.addPersonalParticles(shiny, ModParticles.SPARKLE, shiny.getX() + offsetX, shiny.getBodyY(0.5) + offsetY, shiny.getZ() + offsetZ, 0, 0, 0);
            }
        }
    }
}
