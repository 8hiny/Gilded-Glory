package shiny.gildedglory.client.particle.effect;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;

import java.util.Locale;

public class IntParticleEffect implements ParticleEffect {

    public static final ParticleEffect.Factory<IntParticleEffect> PARAMETERS_FACTORY = new ParticleEffect.Factory<>() {
        public IntParticleEffect read(ParticleType<IntParticleEffect> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            int value = reader.readInt();
            return new IntParticleEffect(type, value);
        }
        public IntParticleEffect read(ParticleType<IntParticleEffect> type, PacketByteBuf buf) {
            return new IntParticleEffect(type, buf.readInt());
        }
    };

    private final ParticleType<IntParticleEffect> type;
    private final int value;

    public IntParticleEffect(ParticleType<IntParticleEffect> type, int value) {
        this.type = type;
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    @Override
    public ParticleType<?> getType() {
        return this.type;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeInt(this.value);
    }

    @Override
    public String asString() {
        return String.format(Locale.ROOT,
                "%s %d",
                Registries.PARTICLE_TYPE.getId(this.type),
                this.value
        );
    }
}
