package shiny.gildedglory.client.old;

import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.MathHelper;
import shiny.gildedglory.common.item.ChargeableWeapon;
import shiny.gildedglory.common.registry.item.ModItems;
import shiny.gildedglory.common.registry.sound.ModSounds;

public class ItemUseSoundInstance extends MovingSoundInstance {

    private final LivingEntity source;
    private final Item item;
    private final int increaseDuration;
    private final float volumeModifier;
    private final float pitchModifier;
    private final boolean onRelease;
    private int useTicks;
    private int lastUseTicks;

    public ItemUseSoundInstance(LivingEntity source, Item item, SoundEvent sound, float baseVolume, float basePitch, float volumeModifier, float pitchModifier, int increaseDuration, boolean onRelease) {
        super(sound, SoundCategory.PLAYERS, SoundInstance.createRandom());

        this.source = source;
        this.item = item;
        this.volumeModifier = volumeModifier;
        this.pitchModifier = pitchModifier;
        this.increaseDuration = increaseDuration;
        this.onRelease = onRelease;

        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = baseVolume;
        this.pitch = basePitch;
        this.useTicks = 0;
        this.lastUseTicks = this.useTicks;
        this.attenuationType = AttenuationType.LINEAR;

        this.x = (float) this.source.getX();
        this.y = (float) this.source.getY();
        this.z = (float) this.source.getZ();
    }

    //Should make this stop playing once a different item stack is selected
    @Override
    public void tick() {
        boolean bl;
        if (this.onRelease) {
            bl = ChargeableWeapon.getCharge(this.source) > 10;
        }
        else {
            bl = this.source.getActiveItem().isOf(this.item);
        }

        if (this.source.isPartOfGame() && bl) {
            this.x = (float) this.source.getX();
            this.y = (float) this.source.getY();
            this.z = (float) this.source.getZ();

            if (this.useTicks < increaseDuration) {
                this.volume = Math.min(2.0f, this.volume + this.volumeModifier);
                this.pitch = Math.min(2.0f, this.pitch + this.pitchModifier);
            }
            this.lastUseTicks = useTicks;
        }
        else {
            if (this.useTicks - this.lastUseTicks <= 10) {
                this.lerpEnd();
            }
            else if (this.useTicks > 0) {
                this.setDone();
            }
        }
        this.useTicks++;
    }

    private void lerpEnd() {
        this.volume = MathHelper.lerp(0.1f, this.volume, 0.0f);
        this.pitch = MathHelper.lerp(0.1f, this.pitch, 0.0f);
    }

    @Override
    public boolean shouldAlwaysPlay() {
        return true;
    }

    public enum Sounds {

        AURADEUS(ModItems.AURADEUS, ModSounds.AURADEUS_HUM, 0.0f, 0.5f, 0.08f, 0.0333333f, 15, false),
        SWORDSPEAR_CHARGING(ModItems.SWORDSPEAR, ModSounds.SWORDSPEAR_CHARGE, 0.5f, 0.3f, 0.04f, 0.017f, 100, false),
        SWORDSPEAR_FIRING(ModItems.SWORDSPEAR, ModSounds.SWORDSPEAR_FIRING, 1.0f, 1.0f, 0.0f, 0.0f, 0, true);

        private final Item item;
        private final SoundEvent sound;
        private final int duration;
        private final float baseVolume;
        private final float basePitch;
        private final float volumeModifier;
        private final float pitchModifier;
        private final boolean onRelease;

        Sounds(Item item, SoundEvent sound, float baseVolume, float basePitch, float volumeModifier, float pitchModifier, int duration, boolean onRelease) {
            this.item = item;
            this.sound = sound;
            this.baseVolume = baseVolume;
            this.basePitch = basePitch;
            this.volumeModifier = volumeModifier;
            this.pitchModifier = pitchModifier;
            this.duration = duration;
            this.onRelease = onRelease;
        }

        public ItemUseSoundInstance get(LivingEntity source) {
            return new ItemUseSoundInstance(source, item, sound, baseVolume, basePitch, volumeModifier, pitchModifier, duration, onRelease);
        }
    }
}
