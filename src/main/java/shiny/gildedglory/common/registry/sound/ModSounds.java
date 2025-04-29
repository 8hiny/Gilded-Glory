package shiny.gildedglory.common.registry.sound;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import shiny.gildedglory.GildedGlory;

public class ModSounds {

    public static final SoundEvent SICKLE_CRIT = register("item.sickle.attack.crit");
    public static final SoundEvent GILDED_HORN = register("item.gilded_horn.blow");

    public static final SoundEvent AURADEUS_SLASH = register("item.auradeus.attack.slash");
    public static final SoundEvent AURADEUS_SLASH_PROJECTILE = register("item.auradeus.attack.projectile");
    public static final SoundEvent AURADEUS_HUM = register("item.auradeus.hum");

    public static final SoundEvent SWORDSPEAR_CRIT = register("item.swordspear.attack.crit");
    public static final SoundEvent SWORDSPEAR_SLASH = register("item.swordspear.attack.slash");
    public static final SoundEvent SWORDSPEAR_CHARGE = register("item.swordspear.charge");
    public static final SoundEvent SWORDSPEAR_FIRE = register("item.swordspear.fire");
    public static final SoundEvent SWORDSPEAR_FIRING = register("item.swordspear.firing");

    public static final SoundEvent IRAEDEUS_HUM = register("item.iraedeus.hum");
    public static final SoundEvent IRAEDEUS_SPIN = register("item.iraedeus.spin");
    public static final SoundEvent IRAEDEUS_CRIT_LOW = register("item.iraedeus.attack.crit.low");
    public static final SoundEvent IRAEDEUS_CRIT_MEDIUM = register("item.iraedeus.attack.crit.medium");
    public static final SoundEvent IRAEDEUS_CRIT_HIGH = register("item.iraedeus.attack.crit.high");
    public static final SoundEvent IRAEDEUS_SWEEP_LOW = register("item.iraedeus.attack.sweep.low");
    public static final SoundEvent IRAEDEUS_SWEEP_MEDIUM = register("item.iraedeus.attack.sweep.medium");
    public static final SoundEvent IRAEDEUS_SWEEP_HIGH = register("item.iraedeus.attack.sweep.high");
    public static final SoundEvent IRAEDEUS_KNOCKBACK_LOW = register("item.iraedeus.attack.knockback.low");
    public static final SoundEvent IRAEDEUS_KNOCKBACK_MEDIUM = register("item.iraedeus.attack.knockback.medium");
    public static final SoundEvent IRAEDEUS_KNOCKBACK_HIGH = register("item.iraedeus.attack.knockback.high");
    public static final SoundEvent IRAEDEUS_SLASH = register("item.iraedeus.attack.slash");
    public static final SoundEvent IRAEDEUS_THROW = register("item.iraedeus.throw");
    public static final SoundEvent IRAEDEUS_HIT = register("item.iraedeus.hit");
    public static final SoundEvent IRAEDEUS_PARRY = register("item.iraedeus.parry");

    private static SoundEvent register(String name) {
        Identifier id = GildedGlory.id(name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void registerModSounds() {
    }
}
