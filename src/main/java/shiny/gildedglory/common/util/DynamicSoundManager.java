package shiny.gildedglory.common.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import shiny.gildedglory.GildedGlory;
import shiny.gildedglory.client.sound.DynamicSoundInstance;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DynamicSoundManager implements SoundInstanceCallback {

    private static DynamicSoundManager instance;
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private final List<DynamicSoundInstance> sounds = new ArrayList<>();
    private final List<Pair<Integer, DynamicSoundInstance>> nextSounds = new ArrayList<>();

    private DynamicSoundManager() {
    }

    public static DynamicSoundManager getInstance() {
        if (instance == null) {
            instance = new DynamicSoundManager();
        }
        return instance;
    }

    public <T extends DynamicSoundInstance> void onFinished(T sound) {
        this.stop(sound);
    }

    public <T extends DynamicSoundInstance> void play(T sound) {
        if (!this.isAlreadyPlaying(sound)) {
            if (!client.getSoundManager().isPlaying(sound)) {
                GildedGlory.LOGGER.debug("[DynamicSoundManager] Playing sound " + sound);
                client.getSoundManager().play(sound);
                this.sounds.add(sound);
            }
        }
        else {
            GildedGlory.LOGGER.debug("[DynamicSoundManager] Sound " + sound + " is already playing!");
            DynamicSoundInstance oldSound = this.getSound(sound.getId(), sound.getSource());
            GildedGlory.LOGGER.debug("[DynamicSoundManager] Forcibly stopping " + oldSound);
            oldSound.forceEnd();

            client.getSoundManager().play(sound);
            this.sounds.add(sound);
        }
    }

    public <T extends DynamicSoundInstance> void playDelayed(T sound, int delay) {
        if (!this.shouldPlayNext(sound)) {
            GildedGlory.LOGGER.debug("[DynamicSoundManager] Storing delayed " + sound + " for " + delay + " ticks");
            this.nextSounds.add(new Pair<>(delay, sound));
        }
    }

    public <T extends DynamicSoundInstance> void stop(T sound) {
        GildedGlory.LOGGER.debug("[DynamicSoundManager] Stopping sound " + sound);
        this.sounds.remove(sound);
        client.getSoundManager().stop(sound);
    }

    public DynamicSoundInstance getSound(Identifier id) {
        for (DynamicSoundInstance sound : this.sounds) {
            if (id.equals(sound.getId())) {
                return sound;
            }
        }
        return null;
    }

    public DynamicSoundInstance getSound(Identifier id, DynamicSoundSource source) {
        for (DynamicSoundInstance sound : this.sounds) {
            if (id.equals(sound.getId()) && source == sound.getSource()) {
                return sound;
            }
        }
        return null;
    }

    //Makes sure that a specific sound can't be played from the same source at the same time
    public boolean isAlreadyPlaying(DynamicSoundInstance sound) {
        return getSound(sound.getId(), sound.getSource()) != null;
    }

    public <T extends DynamicSoundInstance> boolean shouldPlayNext(T sound) {
        for (Pair<Integer, DynamicSoundInstance> pair : this.nextSounds) {
            if (pair.getRight() == sound) return true;
        }
        return false;
    }

    public void tick() {
        if (!this.nextSounds.isEmpty()) {
            for (Iterator<Pair<Integer, DynamicSoundInstance>> iterator = this.nextSounds.iterator(); iterator.hasNext();) {
                Pair<Integer, DynamicSoundInstance> pair = iterator.next();

                if (pair.getLeft() > 0) {
                    pair.setLeft(pair.getLeft() - 1);
                }
                else {
                    DynamicSoundInstance sound = pair.getRight();
                    this.play(sound);
                    iterator.remove();
                }
            }
        }
    }
}
