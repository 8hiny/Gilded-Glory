package shiny.gildedglory.client.sound;

import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.NotNull;
import shiny.gildedglory.GildedGlory;
import shiny.gildedglory.common.entity.IraedeusEntity;
import shiny.gildedglory.common.registry.item.ModItems;
import shiny.gildedglory.common.registry.sound.ModSounds;
import shiny.gildedglory.common.util.DynamicSoundManager;
import shiny.gildedglory.common.util.DynamicSoundSource;

public class DynamicSounds {

    private static final DefaultedList<DynamicSoundInstance.Builder> sounds = DefaultedList.of();

    private final static PredicatedLoopingSoundInstance.Builder AURADEUS_HUM;
    private final static PredicatedLoopingSoundInstance.Builder SWORDSPEAR_CHARGING;
    private final static PredicatedLoopingSoundInstance.Builder SWORDSPEAR_FIRING;
    private final static PredicatedLoopingSoundInstance.Builder IRAEDEUS_HUM;
    private final static PredicatedLoopingSoundInstance.Builder IRAEDEUS_SPIN;

    static {
        AURADEUS_HUM = new PredicatedLoopingSoundInstance.Builder(
                GildedGlory.id("auradeus_hum"),
                ModSounds.AURADEUS_HUM,
                SoundCategory.PLAYERS,
                0.0f, 0.6f,
                0.0f, 0.0f,
                2.0f, 1.0f,
                15, 10,
                false, true,
                false, source -> source.isUsing(ModItems.AURADEUS),
                DynamicSoundManager.getInstance()
        );
        SWORDSPEAR_CHARGING = new PredicatedLoopingSoundInstance.Builder(
                GildedGlory.id("swordspear_charging"),
                ModSounds.SWORDSPEAR_CHARGE,
                SoundCategory.PLAYERS,
                0.5f, 0.3f,
                0.0f, 0.0f,
                2.0f, 2.0f,
                100, 10,
                false, true,
                false, source -> source.isUsing(ModItems.SWORDSPEAR),
                DynamicSoundManager.getInstance()
        );
        SWORDSPEAR_FIRING = new PredicatedLoopingSoundInstance.Builder(
                GildedGlory.id("swordspear_firing"),
                ModSounds.SWORDSPEAR_FIRING,
                SoundCategory.PLAYERS,
                3.5f, 1.0f,
                0.0f, 0.0f,
                3.5f, 1.0f,
                0, 15,
                false, true,
                false, source -> source.getCharge() > 10 && source.isHolding(ModItems.SWORDSPEAR, false),
                DynamicSoundManager.getInstance()
        );
        IRAEDEUS_HUM = new PredicatedLoopingSoundInstance.Builder(
                GildedGlory.id("iraedeus_hum"),
                ModSounds.IRAEDEUS_HUM,
                SoundCategory.PLAYERS,
                0.0f, 1.0f,
                0.0f, 1.0f,
                0.8f, 1.0f,
                0, 100,
                true, false,
                true, source -> source.getCharge() > 0 && source.isHolding(ModItems.IRAEDEUS, false),
                DynamicSoundManager.getInstance()
        );
        IRAEDEUS_SPIN = new PredicatedLoopingSoundInstance.Builder(
                GildedGlory.id("iraedeus_spin"),
                ModSounds.IRAEDEUS_SPIN,
                SoundCategory.PLAYERS,
                1.0f, 1.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                0, 4,
                false, false,
                false, source -> source instanceof IraedeusEntity,
                DynamicSoundManager.getInstance()
        );

        sounds.add(AURADEUS_HUM);
        sounds.add(SWORDSPEAR_CHARGING);
        sounds.add(SWORDSPEAR_FIRING);
        sounds.add(IRAEDEUS_HUM);
        sounds.add(IRAEDEUS_SPIN);
    }

    public static DynamicSoundInstance get(Identifier id, @NotNull DynamicSoundSource source) {
        for (DynamicSoundInstance.Builder builder : sounds) {
            if (id.equals(builder.getId())) return builder.build(source);
        }
        return null;
    }
}
