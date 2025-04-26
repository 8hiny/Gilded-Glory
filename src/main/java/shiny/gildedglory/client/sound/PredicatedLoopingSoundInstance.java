package shiny.gildedglory.client.sound;

import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import shiny.gildedglory.common.util.DynamicSoundSource;
import shiny.gildedglory.common.util.SoundInstanceCallback;

import java.util.function.Predicate;

public class PredicatedLoopingSoundInstance extends DynamicSoundInstance {

    private final Predicate<DynamicSoundSource> predicate;
    private boolean remain;

    /**
     * A DynamicSoundInstance with a predicate attached to its source.
     * @param predicate The predicate for this sound instance to play. Accepts a DynamicSoundSource.
     */
    public PredicatedLoopingSoundInstance(
            Identifier id,
            SoundEvent sound, SoundCategory category,
            float baseVolume, float basePitch,
            float minVolume, float minPitch,
            float maxVolume, float maxPitch,
            int startDuration, int endDuration,
            boolean interpolate, boolean affectPitch,
            boolean remain,
            Predicate<DynamicSoundSource> predicate,
            DynamicSoundSource source, SoundInstanceCallback callback
    ) {
        super(id, sound, category, baseVolume, basePitch, minVolume, minPitch, maxVolume, maxPitch, startDuration, endDuration, interpolate, affectPitch, source, callback);

        this.predicate = predicate.and(DynamicSoundSource::canPlay);
        this.remain = remain;
    }

    @Override
    public void tickState() {
        super.tickState();

        switch (this.getState()) {
            case STARTING, PLAYING -> {
                if (this.tick > 0 && !this.canContinue()) {
                    this.end();
                }
            }
            case STOPPING -> {
                if (this.remain && this.canContinue()) {
                    this.setState(State.PLAYING);
                }
            }
        }
    }

    public boolean canContinue() {
        return this.predicate.test(this.getSource());
    }

    @Override
    public void forceEnd() {
        super.forceEnd();
        this.remain = false;
    }

    public static class Builder extends DynamicSoundInstance.Builder {

        private final Predicate<DynamicSoundSource> predicate;
        private final boolean remain;

        public Builder(
                Identifier id,
                SoundEvent sound, SoundCategory category,
                float baseVolume, float basePitch,
                float minVolume, float minPitch,
                float maxVolume, float maxPitch,
                int startDuration, int endDuration,
                boolean interpolate, boolean affectPitch,
                boolean remain,
                Predicate<DynamicSoundSource> predicate,
                SoundInstanceCallback callback
        ) {
            super(id, sound, category, baseVolume, basePitch, minVolume, minPitch, maxVolume, maxPitch, startDuration, endDuration, interpolate, affectPitch, callback);

            this.predicate = predicate;
            this.remain = remain;
        }

        public PredicatedLoopingSoundInstance build(DynamicSoundSource source) {
            return new PredicatedLoopingSoundInstance(this.id, this.sound, this.category, this.baseVolume, this.basePitch, this.minVolume, this.minPitch, this.maxVolume, this.maxPitch, this.startDuration, this.endDuration, this.interpolate, this.affectPitch, this.remain, this.predicate, source, this.callback);
        }
    }
}
