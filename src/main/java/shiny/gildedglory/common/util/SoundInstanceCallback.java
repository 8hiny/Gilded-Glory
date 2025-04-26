package shiny.gildedglory.common.util;

import shiny.gildedglory.client.sound.DynamicSoundInstance;

public interface SoundInstanceCallback {

    <T extends DynamicSoundInstance> void onFinished(T sound);
}
