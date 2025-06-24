package shiny.gildedglory.client.sound;

import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Pair;
import net.minecraft.util.math.MathHelper;
import shiny.gildedglory.common.util.DynamicSoundSource;
import shiny.gildedglory.common.util.SoundInstanceCallback;

public class DynamicSoundInstance extends MovingSoundInstance {

    protected final SoundInstanceCallback callback;
    private DynamicSoundSource source;
    private State state;
    private State lastState;
    private boolean playing;
    protected final SoundEvent soundEvent;
    protected final int startDuration;
    protected final int endDuration;
    protected final float baseVolume;
    protected final float basePitch;
    protected final float minVolume;
    protected final float minPitch;
    protected final float maxVolume;
    protected final float maxPitch;
    protected float lastVolume;
    protected float lastPitch;
    protected float lastStress;
    protected int tick;
    protected int lastTick;
    protected int transitionTick;
    protected int lastTransitionTick;
    protected final boolean interpolate;
    protected final boolean affectPitch;

    /**
     * A looping sound instance which can be attached to a DynamicSoundSource.
     * Volume and pitch of the sound instance are interpolated between their min and max values during their start and end phases, preventing an abrupt start or end of the sound.
     * Sound instances extending this class should define a condition to prevent the sound from playing indefinitely.
     * @param baseVolume The volume of the sound when it starts playing
     * @param basePitch The pitch of the sound when it starts playing
     * @param minVolume The minimum volume of the sound. Should not be lower then 0
     * @param minPitch The minimum volume of the sound. Should not be lower then 0
     * @param startDuration The duration during which the sound approaches its maximum volume and pitch when starting
     * @param endDuration The duration during which the sound approaches its minimum volume and pitch when ending
     * @param interpolate Whether the sound should interpolate its volume and pitch between state changes
     * @param affectPitch Whether the pitch should be modulated
     */
    protected DynamicSoundInstance(
            SoundEvent sound, SoundCategory category,
            float baseVolume, float basePitch,
            float minVolume, float minPitch,
            float maxVolume, float maxPitch,
            int startDuration, int endDuration,
            boolean interpolate, boolean affectPitch,
            SoundInstanceCallback callback
    ) {
        super(sound, category, SoundInstance.createRandom());
        this.soundEvent = sound;
        this.playing = false;

        this.repeat = true;
        this.repeatDelay = 0;
        this.attenuationType = AttenuationType.LINEAR;
        this.interpolate = interpolate;
        this.affectPitch = affectPitch;

        this.baseVolume = baseVolume;
        this.basePitch = basePitch;
        this.volume = baseVolume;
        this.pitch = basePitch;
        this.lastVolume = baseVolume;
        this.lastPitch = basePitch;

        this.callback = callback;
        this.state = State.STARTING;
        this.lastState = this.state;
        this.startDuration = startDuration;
        this.endDuration = endDuration;

        this.minVolume = minVolume;
        this.minPitch = minPitch;
        this.maxVolume = maxVolume;
        this.maxPitch = maxPitch;
        this.lastStress = 0.0f;

        this.tick = 0;
        this.lastTick = 0;
        this.transitionTick = 0;
        this.lastTransitionTick = 0;

        this.setPos();
    }

    @Override
    public void tick() {
        if (this.playing && !this.isDone()) {
            this.setPos();
            this.tickState();
            if (this.state != this.lastState) {
                this.onStateChanged(5);
            }
            this.lastState = this.state;
            this.modulate();
            this.tick++;
            if (this.state != State.STOPPING) this.lastTick = this.tick;
            this.lastStress = this.getStress();
        }
    }

    public void setPos() {
        if (this.source != null) {
            this.x = this.source.getX();
            this.y = this.source.getY();
            this.z = this.source.getZ();
        }
    }

    public void update() {
        this.lastVolume = this.volume;
        this.lastPitch = this.pitch;
    }

    /**
     * Updates values for the current state and transitions between them.
     */
    public void tickState() {
        switch (this.state) {
            case STARTING -> {
                if (this.tick > this.startDuration) {
                    this.state = State.PLAYING;
                }
            }
            case STOPPING -> {
                if (this.tick - this.lastTick > this.endDuration) {
                    this.state = State.STOPPED;
                }
            }
            case STOPPED -> {
                this.callback.onFinished(this);
                this.setDone();
            }
        }
    }

    /**
     * Starts interpolating from the last volume and pitch to the next. Values greater than 0 interpolate downwards, values less than 0 upwards.
     */
    public void onStateChanged(int duration) {
        if (this.interpolate) {
            this.transitionTick = duration;
            this.lastTransitionTick = duration;
        }
        this.update();
    }

    /**
     * Modulates the volume and pitch of this sound based on the current state, the stress value of the sound source, and whether values should be interpolated between those states.
     */
    public void modulate() {
        Pair<Float, Float> pair = new Pair<>(this.lastVolume, this.lastPitch);

        if (this.state == State.PLAYING) pair = this.modulateForStress();
        else pair = this.modulateForState(pair);

        pair = this.limit(pair);

        float nextVolume = pair.getLeft();
        float nextPitch = pair.getRight();

        if (this.interpolate && this.transitionTick > 0) {
            this.volume = MathHelper.lerp(1 - (float) this.transitionTick / this.lastTransitionTick, this.lastVolume, nextVolume);
            if (this.affectPitch) this.pitch = MathHelper.lerp(1 - (float) this.transitionTick / this.lastTransitionTick, this.lastPitch, nextPitch);

            this.transitionTick--;
        }
        else {
            this.volume = nextVolume;
            if (this.affectPitch) this.pitch = nextPitch;
        }
    }

    /**
     * Interpolates a pair of this sound's volume and pitch to achieve a fading-in and fading-out of the sound.
     */
    public Pair<Float, Float> modulateForState(Pair<Float, Float> pair) {
        switch (this.state) {
            case STARTING -> {
                return this.lerpAbsolute((float) this.tick / this.startDuration);
            }
            case STOPPING -> {
                return this.lerpRelative(pair,(float) (this.tick - this.lastTick) / this.endDuration, false);
            }
        }
        return pair;
    }

    /**
     * Returns a pair of this sound's volume and pitch as affected by the sound source's stress value.
     * Stress should be a normalized float (0-1). Higher values affect the volume and pitch more.
     */
    public Pair<Float, Float> modulateForStress() {
        float stress = this.getStress();

        if (Math.abs(this.lastStress - stress) > 0.2f) {
            this.onStateChanged(5);
        }
        return this.lerpAbsolute(1.0f - stress);
    }

    /**
     * Returns a pair of this sound's volume and pitch between their respective minimum and maximum values by a normalized float (0-1).
     */
    public Pair<Float, Float> lerpAbsolute(float delta) {
        float volume = MathHelper.lerp(delta, this.minVolume, this.maxVolume);
        float pitch = MathHelper.lerp(delta, this.minPitch, this.maxPitch);
        return new Pair<>(volume, pitch);
    }

    /**
     * Interpolates a pair of this sound's volume and pitch towards either their minimum or maximum values.
     * Is mainly used to interpolate from the sound's last volume and pitch to the next.
     * @param up Whether the given values should interpolate towards their respective maximum of minimum values
     */
    public Pair<Float, Float> lerpRelative(Pair<Float, Float> pair, float delta, boolean up) {
        float volume = MathHelper.lerp(delta, pair.getLeft(), up ? this.maxVolume : this.minVolume);
        float pitch = MathHelper.lerp(delta, pair.getRight(), up ? this.maxPitch : this.minPitch);
        return new Pair<>(volume, pitch);
    }

    /**
     * Limits a pair of this sound's volume and pitch so that they stay between 0 and their respective maximum values.
     * Additionally rounds values down to 0.0 to avoid any miniscule numbers.
     */
    public Pair<Float, Float> limit(Pair<Float, Float> pair) {
        float volume = MathHelper.clamp(pair.getLeft(), 0.0f, this.maxVolume);
        float pitch = MathHelper.clamp(pair.getRight(), 0.0f, this.maxPitch);

        if (Float.isNaN(volume) || Float.isInfinite(volume)) volume = 0.0f;
        if (Float.isNaN(pitch) || Float.isInfinite(pitch)) pitch = 0.0f;

        if (volume < 0.01f) volume = 0.0f;
        if (pitch < 0.01f) pitch = 0.0f;

        return new Pair<>(volume, pitch);
    }

    public void setState(State state) {
        this.state = state;
    }

    public void end() {
        this.state = State.STOPPING;
    }

    /**
     * Forcibly sets the ending state of this sound instance.
     * Sound instances with a play condition should override this to negate that condition.
     * @see PredicatedLoopingSoundInstance#forceEnd()
     */
    public void forceEnd() {
        this.state = State.STOPPING;
    }

    public State getState() {
        return this.state;
    }

    public DynamicSoundSource getSource() {
        return this.source;
    }

    public float getStress() {
        if (this.source != null) return this.source.getStress();
        return 0.0f;
    }

    @Override
    public boolean shouldAlwaysPlay() {
        return true;
    }

    /** Returns a copy of this sound instance. Used to retrieve new DynamicSoundInstance objects without modifying those registered in the registry.
     * Subclasses should override this so return themselves.
     * @see PredicatedLoopingSoundInstance#copy()
     */
    public DynamicSoundInstance copy() {
        return new DynamicSoundInstance(
                this.soundEvent, this.category,
                this.baseVolume, this.basePitch,
                this.minVolume, this.minPitch,
                this.maxVolume, this.maxPitch,
                this.startDuration, this.endDuration,
                this.interpolate, this.affectPitch,
                this.callback
        );
    }

    /// Builds this sound instance with the specified DynamicSoundSource.
    public DynamicSoundInstance build(DynamicSoundSource source) {
        DynamicSoundInstance sound = this.copy();
        sound.source = source;
        sound.playing = true;
        return sound;
    }

    public enum State {
        STARTING(),
        PLAYING(),
        STOPPING(),
        STOPPED()
    }
}
