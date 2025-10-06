package shiny.gildedglory.common.registry;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import shiny.gildedglory.GildedGlory;
import shiny.gildedglory.client.pose.CustomArmPose;
import shiny.gildedglory.client.sound.DynamicSoundInstance;
import shiny.gildedglory.client.use_action.CustomUseAction;

public class ModRegistries {

    public static final RegistryKey<Registry<CustomArmPose>> armPoseKey = RegistryKey.ofRegistry(GildedGlory.id("custom_arm_pose"));
    public static final Registry<CustomArmPose> CUSTOM_ARM_POSE = FabricRegistryBuilder.createSimple(armPoseKey)
            .attribute(RegistryAttribute.SYNCED)
            .buildAndRegister();
    public static final RegistryKey<Registry<CustomUseAction>> useActionKey = RegistryKey.ofRegistry(GildedGlory.id("custom_use_action"));
    public static final Registry<CustomUseAction> CUSTOM_USE_ACTION = FabricRegistryBuilder.createSimple(useActionKey)
            .attribute(RegistryAttribute.SYNCED)
            .buildAndRegister();
    public static final RegistryKey<Registry<DynamicSoundInstance>> dynamicSoundKey = RegistryKey.ofRegistry(GildedGlory.id("dynamic_sound_instance"));
    public static final Registry<DynamicSoundInstance> DYNAMIC_SOUND_INSTANCE = FabricRegistryBuilder.createSimple(dynamicSoundKey)
            .attribute(RegistryAttribute.SYNCED)
            .buildAndRegister();

    public static void init() {
    }
}
