package shiny.gildedglory.client.sound;

import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import shiny.gildedglory.GildedGlory;
import shiny.gildedglory.common.entity.IraedeusEntity;
import shiny.gildedglory.common.registry.ModRegistries;
import shiny.gildedglory.common.registry.item.ModItems;
import shiny.gildedglory.common.registry.sound.ModSounds;
import shiny.gildedglory.common.util.DynamicSoundManager;
import shiny.gildedglory.common.util.DynamicSoundSource;

public class DynamicSounds {

    private static final DynamicSoundInstance AURADEUS_HUM = register("auradeus_hum", new PredicatedLoopingSoundInstance(
            ModSounds.AURADEUS_HUM,
            SoundCategory.PLAYERS,
            0.0f, 0.6f,
            0.0f, 0.0f,
            2.0f, 1.0f,
            15, 10,
            false, true,
            false, source -> source.isUsing(ModItems.AURADEUS),
            DynamicSoundManager.getInstance()
    ));
    private static final DynamicSoundInstance SWORDSPEAR_CHARGING = register("swordspear_charging", new PredicatedLoopingSoundInstance(
            ModSounds.SWORDSPEAR_CHARGE,
            SoundCategory.PLAYERS,
            0.5f, 0.3f,
            0.0f, 0.0f,
            2.0f, 2.0f,
            100, 10,
            false, true,
            false, source -> source.isUsing(ModItems.SWORDSPEAR),
            DynamicSoundManager.getInstance()
    ));
    private static final DynamicSoundInstance SWORDSPEAR_FIRING = register("swordspear_firing", new PredicatedLoopingSoundInstance(
            ModSounds.SWORDSPEAR_FIRING,
            SoundCategory.PLAYERS,
            3.5f, 1.0f,
            0.0f, 0.0f,
            3.5f, 1.0f,
            0, 15,
            false, true,
            false, source -> source.getCharge() > 10 && source.isHolding(ModItems.SWORDSPEAR, false),
            DynamicSoundManager.getInstance()
    ));
    private static final DynamicSoundInstance IRAEDEUS_HUM = register("iraedeus_hum", new PredicatedLoopingSoundInstance(
            ModSounds.IRAEDEUS_HUM,
            SoundCategory.PLAYERS,
            0.0f, 1.0f,
            0.0f, 1.0f,
            0.8f, 1.0f,
            0, 100,
            true, false,
            true, source -> source.getCharge() > 0 && source.isHolding(ModItems.IRAEDEUS, false),
            DynamicSoundManager.getInstance()
    ));
    private static final DynamicSoundInstance IRAEDEUS_SPIN = register("iraedeus_spin", new PredicatedLoopingSoundInstance(
            ModSounds.IRAEDEUS_SPIN,
            SoundCategory.PLAYERS,
            1.0f, 1.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            0, 4,
            false, false,
            false, source -> source instanceof IraedeusEntity,
            DynamicSoundManager.getInstance()
    ));

    public static DynamicSoundInstance register(String name, DynamicSoundInstance sound) {
        return Registry.register(ModRegistries.DYNAMIC_SOUND_INSTANCE, GildedGlory.id(name), sound);
    }

    public static DynamicSoundInstance get(Identifier id, @NotNull DynamicSoundSource source) {
        DynamicSoundInstance sound = ModRegistries.DYNAMIC_SOUND_INSTANCE.get(id);
        return sound != null ? sound.build(source) : null;
    }

    public static void registerDynamicSounds() {
    }
}
